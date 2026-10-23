package com.eldercare.order.feign;

import com.eldercare.common.core.dto.SendMessageDTO;
import com.eldercare.common.core.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 消息通知服务 Feign 客户端（工单状态变更时推送消息）。
 */
@FeignClient(name = "notify")
public interface NotifyClient {

    @PostMapping("/message/send")
    Result<Void> send(SendMessageDTO dto);
}
