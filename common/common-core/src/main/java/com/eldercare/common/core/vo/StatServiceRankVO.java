package com.eldercare.common.core.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 服务类型排名统计。
 */
@Data
public class StatServiceRankVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String serviceType;

    private Long orderCount;

    private BigDecimal amount;
}
