package com.eldercare.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 服务商信息（OpenFeign 从 user-center 拉取，用于智能派单）。
 */
@Data
public class ProviderVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String providerName;

    /** 可提供服务类型（逗号分隔） */
    private String serviceTypes;

    /** 评分 1-5 */
    private BigDecimal rating;
}
