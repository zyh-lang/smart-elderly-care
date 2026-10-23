package com.eldercare.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 下单请求参数。
 */
@Data
public class OrderCreateDTO {

    @NotNull(message = "服务项目不能为空")
    private Long serviceId;

    private Long elderId;

    private String address;

    private LocalDateTime expectTime;

    private String remark;
}
