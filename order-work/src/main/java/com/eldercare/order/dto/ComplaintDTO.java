package com.eldercare.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 投诉请求参数。
 */
@Data
public class ComplaintDTO {

    @NotNull(message = "工单不能为空")
    private Long workOrderId;

    @NotBlank(message = "投诉原因不能为空")
    private String reason;
}
