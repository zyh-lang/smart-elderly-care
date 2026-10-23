package com.eldercare.order.enums;

/**
 * 订单状态常量。
 */
public final class OrderStatus {

    /** 待派单 */
    public static final String PENDING_DISPATCH = "PENDING_DISPATCH";

    /** 已派单 */
    public static final String DISPATCHED = "DISPATCHED";

    /** 已完成（验收后） */
    public static final String COMPLETED = "COMPLETED";

    /** 已取消 */
    public static final String CANCELLED = "CANCELLED";

    private OrderStatus() {
    }
}
