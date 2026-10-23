package com.eldercare.order.dto;

import com.eldercare.order.entity.ServiceOrder;
import com.eldercare.order.entity.WorkOrder;
import lombok.Data;

import java.io.Serializable;

/**
 * 工单详情（工单 + 关联订单）。
 */
@Data
public class WorkOrderDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private WorkOrder workOrder;

    private ServiceOrder serviceOrder;
}
