package com.eldercare.order.service;

import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.redis.util.DistributedLock;
import com.eldercare.order.dto.EvaluationDTO;
import com.eldercare.order.dto.ProviderVO;
import com.eldercare.order.entity.DispatchRecord;
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
import com.eldercare.order.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 订单与工单服务单元测试（Mockito）。
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ServiceCatalogMapper serviceCatalogMapper;

    @Mock
    private ServiceOrderMapper serviceOrderMapper;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @Mock
    private DispatchRecordMapper dispatchRecordMapper;

    @Mock
    private EvaluationMapper evaluationMapper;

    @Mock
    private ComplaintMapper complaintMapper;

    @Mock
    private UserClient userClient;

    @Mock
    private DistributedLock distributedLock;

    @Mock
    private NotifyService notifyService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("智能派单：应选择综合评分最高的服务商")
    void dispatchShouldPickHighestScoredProvider() {
        ServiceOrder order = new ServiceOrder();
        order.setId(1L);
        order.setServiceType("护理");
        order.setServiceName("上门护理");
        order.setStatus(OrderStatus.PENDING_DISPATCH);
        when(serviceOrderMapper.selectById(1L)).thenReturn(order);
        when(distributedLock.tryLock(anyString(), anyString(), anyLong())).thenReturn(true);
        // 两个服务商：安心 4.8 分、顺康 4.9 分（负载与完成率相同）
        when(userClient.listProviders()).thenReturn(Result.success(Arrays.asList(
                provider(1L, 3L, "安心养老护理服务商", "护理,陪护", "4.8"),
                provider(3L, 6L, "顺康医疗陪护中心", "护理,康复", "4.9"))));
        when(workOrderMapper.selectCount(any())).thenReturn(0L);

        WorkOrder result = orderService.dispatch(1L);

        assertNotNull(result);
        // 评分 4.9 的顺康应胜出
        assertEquals(3L, result.getProviderId());
        assertEquals("顺康医疗陪护中心", result.getProviderName());
        assertEquals(WorkOrderStatus.DISPATCHED, result.getStatus());
        verify(workOrderMapper, times(1)).insert(any(WorkOrder.class));
        verify(dispatchRecordMapper, times(1)).insert(any(DispatchRecord.class));
        verify(distributedLock, times(1)).unlock(anyString(), anyString());
    }

    @Test
    @DisplayName("智能派单：非待派单状态应拒绝（防重复派单）")
    void dispatchShouldRejectNonPendingOrder() {
        ServiceOrder order = new ServiceOrder();
        order.setId(1L);
        order.setStatus(OrderStatus.DISPATCHED);
        when(serviceOrderMapper.selectById(1L)).thenReturn(order);

        BusinessException ex = assertThrows(BusinessException.class, () -> orderService.dispatch(1L));
        assertEquals(ErrorCode.ORDER_STATUS_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("工单状态机：已评价工单不允许再次开始服务")
    void stateMachineShouldRejectInvalidTransition() {
        WorkOrder wo = new WorkOrder();
        wo.setId(1L);
        wo.setStatus(WorkOrderStatus.EVALUATED);
        when(workOrderMapper.selectById(1L)).thenReturn(wo);

        BusinessException ex = assertThrows(BusinessException.class, () -> orderService.startWork(1L));
        assertEquals(ErrorCode.WORK_ORDER_STATUS_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("评价：非已完成工单应拒绝评价")
    void evaluateShouldRejectWhenNotCompleted() {
        WorkOrder wo = new WorkOrder();
        wo.setId(1L);
        wo.setStatus(WorkOrderStatus.IN_SERVICE);
        when(workOrderMapper.selectById(1L)).thenReturn(wo);

        EvaluationDTO dto = new EvaluationDTO();
        dto.setWorkOrderId(1L);
        dto.setRating(5);

        BusinessException ex = assertThrows(BusinessException.class, () -> orderService.evaluate(dto));
        assertEquals(ErrorCode.WORK_ORDER_STATUS_ERROR.getCode(), ex.getCode());
    }

    private ProviderVO provider(Long id, Long userId, String name, String types, String rating) {
        ProviderVO vo = new ProviderVO();
        vo.setId(id);
        vo.setUserId(userId);
        vo.setProviderName(name);
        vo.setServiceTypes(types);
        vo.setRating(new BigDecimal(rating));
        return vo;
    }
}
