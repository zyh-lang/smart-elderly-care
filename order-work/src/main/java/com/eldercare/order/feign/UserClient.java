package com.eldercare.order.feign;

import com.eldercare.common.core.result.Result;
import com.eldercare.order.dto.ProviderVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 用户中心服务 Feign 客户端（服务间调用）。
 */
@FeignClient(name = "user-center")
public interface UserClient {

    /** 获取服务商列表（用于智能派单） */
    @GetMapping("/provider/list")
    Result<List<ProviderVO>> listProviders();
}
