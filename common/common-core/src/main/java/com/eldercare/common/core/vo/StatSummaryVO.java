package com.eldercare.common.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 统计汇总数据（order-work 提供，statistics 经 Feign 聚合）。
 */
@Data
public class StatSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 订单总数 */
    private Long orderCount;

    /** 总营收 */
    private BigDecimal totalAmount;

    /** 工单总数 */
    private Long workOrderCount;

    /** 在途工单数（已派单+服务中+待验收） */
    private Long inServiceCount;

    /** 已完成数（含已评价） */
    private Long completedCount;

    /** 已取消数 */
    private Long cancelledCount;

    /** 已投诉数 */
    private Long complainedCount;

    /** 平均满意度（1-5） */
    private BigDecimal avgRating;

    /** 投诉总数 */
    private Long complaintCount;
}
