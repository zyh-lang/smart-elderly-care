package com.eldercare.order.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.constant.CommonConstant;
import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.redis.util.DistributedLock;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.order.dto.ComplaintDTO;
import com.eldercare.order.dto.EvaluationDTO;
import com.eldercare.order.dto.OrderCreateDTO;
import com.eldercare.order.dto.ProviderVO;
import com.eldercare.order.entity.Complaint;
import com.eldercare.order.entity.DispatchRecord;
import com.eldercare.order.entity.Evaluation;
import com.eldercare.order.entity.ServiceCatalog;
import com.eldercare.order.entity.ServiceOrder;
import com.eldercare.order.entity.WorkOrder;
import com.eldercare.order.enums.OrderStatus;
import com.eldercare.order.enums.WorkOrderStatus;
import com.eldercare.order.feign.UserClient;
import com.eldercare.order.mapper.ComplaintMapper;
import com.eldercare.order.mapper.DispatchRecordMapper;
import com.eldercare.order.mapper.EvaluationMapper;
import com.eldercare.order.mapper.ServiceCatalogMapper;
import com.eldercare.order.mapper.ServiceOrderMapper;
import com.eldercare.order.mapper.WorkOrderMapper;
import com.eldercare.order.service.NotifyService;
import com.eldercare.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 订单与工单服务实现。
 */
@Service
public class OrderServiceImpl implements OrderService {

    /** 服务商最大在途工单数（用于负载分计算） */
    private static final int MAX_CAPACITY = 10;

    @Resource
    private ServiceCatalogMapper serviceCatalogMapper;

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private DispatchRecordMapper dispatchRecordMapper;

    @Resource
    private EvaluationMapper evaluationMapper;

    @Resource
    private ComplaintMapper complaintMapper;

    @Resource
    private UserClient userClient;

    @Resource
    private DistributedLock distributedLock;

    @Resource
    private NotifyService notifyService;

    @Override
    public Long createOrder(OrderCreateDTO dto) {
        ServiceCatalog catalog = serviceCatalogMapper.selectById(dto.getServiceId());
        if (catalog == null || !CommonConstant.STATUS_ENABLED.equals(catalog.getStatus())) {
            throw new BusinessException(ErrorCode.SERVICE_NOT_EXIST);
        }
        ServiceOrder order = new ServiceOrder();
        order.setOrderNo(genNo("SO"));
        order.setFamilyUserId(UserContext.getUserId());
        order.setElderId(dto.getElderId());
        order.setServiceId(catalog.getId());
        order.setServiceName(catalog.getServiceName());
        order.setServiceType(catalog.getServiceType());
        order.setAmount(catalog.getPrice());
        order.setAddress(dto.getAddress());
        order.setExpectTime(dto.getExpectTime());
        order.setRemark(dto.getRemark());
        order.setStatus(OrderStatus.PENDING_DISPATCH);
        serviceOrderMapper.insert(order);
        return order.getId();
    }

    /**
     * 智能派单：按多因子加权评分选出最优服务商。
     *
     * 评分公式：score = 0.3*距离分 + 0.25*评分分 + 0.3*负载分 + 0.15*完成率分
     * - 距离分：0.8（简化占位，可扩展为地图/地址距离计算）
     * - 评分分：服务商评分 / 5
     * - 负载分：1 - 在途工单数 / 最大接单数(10)
     * - 完成率分：已完成工单数 / 历史总工单数
     * 前置过滤：服务商可提供服务类型必须包含订单的服务类型。
     */
    @Override
    public WorkOrder dispatch(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_EXIST);
        }
        if (!OrderStatus.PENDING_DISPATCH.equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }

        // 分布式锁防重复派单
        String lockKey = "dispatch:order:" + orderId;
        String lockValue = RandomUtil.randomString(16);
        if (!distributedLock.tryLock(lockKey, lockValue, 10)) {
            throw new BusinessException(ErrorCode.ALREADY_DISPATCHED);
        }
        try {
            ProviderVO best = null;
            BigDecimal bestScore = null;

            // 经 OpenFeign 从 user-center 拉取服务商列表
            Result<List<ProviderVO>> result = userClient.listProviders();
            List<ProviderVO> providers = (result == null || !result.isSuccess())
                    ? Collections.emptyList() : result.getData();

            for (ProviderVO p : providers) {
                // 过滤：服务类型必须匹配
                if (p.getServiceTypes() == null
                        || !Arrays.asList(p.getServiceTypes().split(",")).contains(order.getServiceType())) {
                    continue;
                }
                // 负载分
                long inFlight = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                        .eq(WorkOrder::getProviderId, p.getId())
                        .in(WorkOrder::getStatus, WorkOrderStatus.DISPATCHED,
                                WorkOrderStatus.IN_SERVICE, WorkOrderStatus.WAIT_ACCEPT));
                double loadScore = Math.max(0.0, 1.0 - inFlight / (double) MAX_CAPACITY);
                // 完成率分
                long total = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                        .eq(WorkOrder::getProviderId, p.getId()));
                long completed = workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                        .eq(WorkOrder::getProviderId, p.getId())
                        .in(WorkOrder::getStatus, WorkOrderStatus.COMPLETED, WorkOrderStatus.EVALUATED));
                double completionScore = total == 0 ? 1.0 : completed / (double) total;
                // 评分分
                double ratingScore = p.getRating() == null ? 0.5 : p.getRating().doubleValue() / 5.0;
                // 距离分（简化）
                double distanceScore = 0.8;

                double score = 0.3 * distanceScore + 0.25 * ratingScore + 0.3 * loadScore + 0.15 * completionScore;
                if (bestScore == null || score > bestScore.doubleValue()) {
                    best = p;
                    bestScore = BigDecimal.valueOf(score);
                }
            }

            if (best == null) {
                throw new BusinessException(ErrorCode.NO_AVAILABLE_PROVIDER);
            }

            // 生成工单
            WorkOrder workOrder = new WorkOrder();
            workOrder.setWorkOrderNo(genNo("WO"));
            workOrder.setOrderId(orderId);
            workOrder.setServiceId(order.getServiceId());
            workOrder.setProviderId(best.getId());
            workOrder.setProviderName(best.getProviderName());
            workOrder.setProviderUserId(best.getUserId());
            workOrder.setStatus(WorkOrderStatus.DISPATCHED);
            workOrder.setDispatchTime(LocalDateTime.now());
            workOrderMapper.insert(workOrder);

            // 派单记录
            DispatchRecord record = new DispatchRecord();
            record.setOrderId(orderId);
            record.setWorkOrderId(workOrder.getId());
            record.setProviderId(best.getId());
            record.setScore(bestScore);
            dispatchRecordMapper.insert(record);

            // 更新订单状态
            ServiceOrder update = new ServiceOrder();
            update.setId(orderId);
            update.setStatus(OrderStatus.DISPATCHED);
            serviceOrderMapper.updateById(update);

            // 消息通知：通知服务商与家属
            notifyService.send(best.getUserId(), "新工单通知", "您有新的服务工单：" + order.getServiceName());
            notifyService.send(order.getFamilyUserId(), "订单派单成功", "您的订单已派单给：" + best.getProviderName());

            return workOrder;
        } finally {
            distributedLock.unlock(lockKey, lockValue);
        }
    }

    @Override
    public void startWork(Long workOrderId) {
        WorkOrder wo = getWorkOrder(workOrderId);
        checkStatus(wo, WorkOrderStatus.DISPATCHED);
        checkProvider(wo);
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setStatus(WorkOrderStatus.IN_SERVICE);
        update.setAcceptTime(LocalDateTime.now());
        workOrderMapper.updateById(update);
        // 通知家属服务已开始
        ServiceOrder order = serviceOrderMapper.selectById(wo.getOrderId());
        notifyService.send(order.getFamilyUserId(), "服务已开始", "服务商已开始为您提供「" + order.getServiceName() + "」服务");
    }

    @Override
    public void finishWork(Long workOrderId) {
        WorkOrder wo = getWorkOrder(workOrderId);
        checkStatus(wo, WorkOrderStatus.IN_SERVICE);
        checkProvider(wo);
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setStatus(WorkOrderStatus.WAIT_ACCEPT);
        update.setFinishTime(LocalDateTime.now());
        workOrderMapper.updateById(update);
        // 通知家属服务完成
        ServiceOrder order = serviceOrderMapper.selectById(wo.getOrderId());
        notifyService.send(order.getFamilyUserId(), "服务已完成", "服务已完成，请及时验收");
    }

    @Override
    public void acceptWork(Long workOrderId) {
        WorkOrder wo = getWorkOrder(workOrderId);
        checkStatus(wo, WorkOrderStatus.WAIT_ACCEPT);
        checkFamilyOrAdmin(wo);
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setStatus(WorkOrderStatus.COMPLETED);
        workOrderMapper.updateById(update);
        // 同步订单状态为已完成（总营收按已完成订单统计）
        ServiceOrder orderUpdate = new ServiceOrder();
        orderUpdate.setId(wo.getOrderId());
        orderUpdate.setStatus(OrderStatus.COMPLETED);
        serviceOrderMapper.updateById(orderUpdate);
        // 通知服务商工单已验收
        notifyService.send(wo.getProviderUserId(), "工单已验收", "您的工单已通过验收");
    }

    @Override
    public void cancelWork(Long workOrderId) {
        WorkOrder wo = getWorkOrder(workOrderId);
        if (!WorkOrderStatus.DISPATCHED.equals(wo.getStatus())
                && !WorkOrderStatus.IN_SERVICE.equals(wo.getStatus())
                && !WorkOrderStatus.WAIT_ACCEPT.equals(wo.getStatus())) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR);
        }
        checkFamilyOrAdmin(wo);
        WorkOrder update = new WorkOrder();
        update.setId(workOrderId);
        update.setStatus(WorkOrderStatus.CANCELLED);
        workOrderMapper.updateById(update);

        ServiceOrder orderUpdate = new ServiceOrder();
        orderUpdate.setId(wo.getOrderId());
        orderUpdate.setStatus(OrderStatus.CANCELLED);
        serviceOrderMapper.updateById(orderUpdate);
        // 通知服务商工单已取消
        notifyService.send(wo.getProviderUserId(), "工单已取消", "您的工单已被取消");
    }

    @Override
    public void evaluate(EvaluationDTO dto) {
        WorkOrder wo = getWorkOrder(dto.getWorkOrderId());
        checkStatus(wo, WorkOrderStatus.COMPLETED);
        checkFamilyOrAdmin(wo);

        Evaluation evaluation = new Evaluation();
        evaluation.setWorkOrderId(wo.getId());
        evaluation.setOrderId(wo.getOrderId());
        evaluation.setUserId(UserContext.getUserId());
        evaluation.setRating(dto.getRating());
        evaluation.setContent(dto.getContent());
        evaluationMapper.insert(evaluation);

        WorkOrder update = new WorkOrder();
        update.setId(wo.getId());
        update.setStatus(WorkOrderStatus.EVALUATED);
        workOrderMapper.updateById(update);
        // 通知服务商收到新评价
        notifyService.send(wo.getProviderUserId(), "收到新评价", "您的服务收到了新的评价");
    }

    @Override
    public void complain(ComplaintDTO dto) {
        WorkOrder wo = getWorkOrder(dto.getWorkOrderId());
        if (!WorkOrderStatus.IN_SERVICE.equals(wo.getStatus())
                && !WorkOrderStatus.WAIT_ACCEPT.equals(wo.getStatus())) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR);
        }
        checkFamilyOrAdmin(wo);

        Complaint complaint = new Complaint();
        complaint.setWorkOrderId(wo.getId());
        complaint.setOrderId(wo.getOrderId());
        complaint.setUserId(UserContext.getUserId());
        complaint.setReason(dto.getReason());
        complaint.setStatus("PENDING");
        complaintMapper.insert(complaint);

        WorkOrder update = new WorkOrder();
        update.setId(wo.getId());
        update.setStatus(WorkOrderStatus.COMPLAINED);
        workOrderMapper.updateById(update);
        // 通知服务商收到投诉
        notifyService.send(wo.getProviderUserId(), "收到投诉", "您的服务收到了一条投诉");
    }

    /* ---------- 私有辅助 ---------- */

    private WorkOrder getWorkOrder(Long id) {
        WorkOrder wo = workOrderMapper.selectById(id);
        if (wo == null) {
            throw new BusinessException(ErrorCode.WORK_ORDER_NOT_EXIST);
        }
        return wo;
    }

    private void checkStatus(WorkOrder wo, String expected) {
        if (!expected.equals(wo.getStatus())) {
            throw new BusinessException(ErrorCode.WORK_ORDER_STATUS_ERROR);
        }
    }

    /** 校验当前用户是工单所属服务商 */
    private void checkProvider(WorkOrder wo) {
        if (!UserContext.getUserId().equals(wo.getProviderUserId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    /** 校验当前用户是下单家属或管理员 */
    private void checkFamilyOrAdmin(WorkOrder wo) {
        ServiceOrder order = serviceOrderMapper.selectById(wo.getOrderId());
        Long userId = UserContext.getUserId();
        if (!userId.equals(order.getFamilyUserId()) && !AuthConstant.ROLE_ADMIN.equals(UserContext.getRole())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private String genNo(String prefix) {
        return prefix + System.currentTimeMillis() + RandomUtil.randomNumbers(4);
    }
}
