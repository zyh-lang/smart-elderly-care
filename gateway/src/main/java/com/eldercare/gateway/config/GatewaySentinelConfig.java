package com.eldercare.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;

/**
 * Sentinel 网关流控配置。
 * 注意：SentinelGatewayFilter 已由 SCA 的 SentinelSCGAutoConfiguration 自动注册，
 * 这里只需加载路由级流控规则，无需（也不能）重复注册同名 Bean。
 */
@Configuration
public class GatewaySentinelConfig {

    @PostConstruct
    public void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();
        // 对 user-center 路由做 QPS 限流示例（阈值 100 QPS，可按需调整）
        rules.add(new GatewayFlowRule("user-center")
                .setCount(100)
                .setIntervalSec(1));
        GatewayRuleManager.loadRules(rules);
    }
}
