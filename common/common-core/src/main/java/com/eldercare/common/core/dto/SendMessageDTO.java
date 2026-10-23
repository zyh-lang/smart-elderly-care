package com.eldercare.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 站内信发送参数（跨服务消息契约，order-work 经 OpenFeign 调用 notify 时使用）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 接收人用户ID */
    private Long userId;

    private String title;

    private String content;

    /** 消息类型：SYSTEM / ORDER / HEALTH */
    private String msgType;
}
