package com.eldercare.order.enums;

/**
 * 工单状态常量（工单状态机）。
 *
 * 流转：DISPATCHED(已派单) -> IN_SERVICE(服务中) -> WAIT_ACCEPT(待验收) -> COMPLETED(已完成) -> EVALUATED(已评价)
 * 分支：任意态可转 CANCELLED(已取消)；服务中/待验收可转 COMPLAINED(已投诉)。
 */
public final class WorkOrderStatus {

    /** 已派单 */
    public static final String DISPATCHED = "DISPATCHED";

    /** 服务中 */
    public static final String IN_SERVICE = "IN_SERVICE";

    /** 待验收 */
    public static final String WAIT_ACCEPT = "WAIT_ACCEPT";

    /** 已完成 */
    public static final String COMPLETED = "COMPLETED";

    /** 已评价 */
    public static final String EVALUATED = "EVALUATED";

    /** 已取消 */
    public static final String CANCELLED = "CANCELLED";

    /** 已投诉 */
    public static final String COMPLAINED = "COMPLAINED";

    private WorkOrderStatus() {
    }
}
