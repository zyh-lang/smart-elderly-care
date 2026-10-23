package com.eldercare.order.service;

import com.eldercare.order.dto.ComplaintDTO;
import com.eldercare.order.dto.EvaluationDTO;
import com.eldercare.order.dto.OrderCreateDTO;
import com.eldercare.order.entity.WorkOrder;

/**
 * 订单与工单服务：下单、智能派单、工单流转、评价投诉。
 */
public interface OrderService {

    Long createOrder(OrderCreateDTO dto);

    WorkOrder dispatch(Long orderId);

    void startWork(Long workOrderId);

    void finishWork(Long workOrderId);

    void acceptWork(Long workOrderId);

    void cancelWork(Long workOrderId);

    void evaluate(EvaluationDTO dto);

    void complain(ComplaintDTO dto);
}
