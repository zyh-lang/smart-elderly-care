package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.result.PageResult;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.order.entity.ServiceOrder;
import com.eldercare.order.entity.WorkOrder;
import com.eldercare.order.mapper.ServiceOrderMapper;
import com.eldercare.order.mapper.WorkOrderMapper;
import com.eldercare.order.service.OrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 工单接口：状态流转与查询。
 */
@RestController
@RequestMapping("/work-order")
public class WorkOrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    /** 服务商开始服务 */
    @PutMapping("/start/{id}")
    public Result<Void> start(@PathVariable Long id) {
        orderService.startWork(id);
        return Result.success();
    }

    /** 服务商完成服务 */
    @PutMapping("/finish/{id}")
    public Result<Void> finish(@PathVariable Long id) {
        orderService.finishWork(id);
        return Result.success();
    }

    /** 家属验收 */
    @PutMapping("/accept/{id}")
    public Result<Void> accept(@PathVariable Long id) {
        orderService.acceptWork(id);
        return Result.success();
    }

    /** 取消工单 */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelWork(id);
        return Result.success();
    }

    /** 工单分页（管理员/工作人员） */
    @GetMapping("/page")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<PageResult<WorkOrder>> page(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size) {
        Page<WorkOrder> page = new Page<>(current, size);
        page = workOrderMapper.selectPage(page, new LambdaQueryWrapper<WorkOrder>()
                .orderByDesc(WorkOrder::getCreateTime));
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    /** 我的工单（服务商按服务商维度，家属/管理员按订单维度） */
    @GetMapping("/my")
    public Result<List<WorkOrder>> my() {
        Long userId = UserContext.getUserId();
        String role = UserContext.getRole();
        List<WorkOrder> list;
        if (AuthConstant.ROLE_PROVIDER.equals(role)) {
            list = workOrderMapper.selectList(new LambdaQueryWrapper<WorkOrder>()
                    .eq(WorkOrder::getProviderUserId, userId)
                    .orderByDesc(WorkOrder::getCreateTime));
        } else {
            List<ServiceOrder> orders = serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>()
                    .eq(ServiceOrder::getFamilyUserId, userId));
            List<Long> orderIds = orders.stream().map(ServiceOrder::getId).collect(Collectors.toList());
            if (orderIds.isEmpty()) {
                list = Collections.emptyList();
            } else {
                list = workOrderMapper.selectList(new LambdaQueryWrapper<WorkOrder>()
                        .in(WorkOrder::getOrderId, orderIds)
                        .orderByDesc(WorkOrder::getCreateTime));
            }
        }
        return Result.success(list);
    }
}
