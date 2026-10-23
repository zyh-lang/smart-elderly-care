package com.eldercare.order.service;

import com.eldercare.common.core.dto.SendMessageDTO;
import com.eldercare.order.feign.NotifyClient;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 本地消息通知服务：异步调用 notify 服务推送站内信。
 * 推送失败仅记录日志，不影响主业务流程。
 */
@Slf4j
@Service
public class NotifyService {

    @Resource
    private NotifyClient notifyClient;

    @Async("notifyExecutor")
    public void send(Long userId, String title, String content) {
        if (userId == null) {
            return;
        }
        try {
            notifyClient.send(new SendMessageDTO(userId, title, content, "ORDER"));
        } catch (Exception e) {
            log.error("消息推送失败: userId={}, title={}", userId, title, e);
        }
    }
}
