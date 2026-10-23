package com.eldercare.common.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 按日趋势统计。
 */
@Data
public class StatTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日期 yyyy-MM-dd */
    private String date;

    /** 当日订单数 */
    private Long orderCount;

    /** 当日营收 */
    private BigDecimal amount;
}
