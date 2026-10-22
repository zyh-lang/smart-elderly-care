package com.eldercare.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 网关服务启动类。
 * scanBasePackages 覆盖 com.eldercare，以扫描 common-security 中的 JwtUtil 等公共组件。
 */
@SpringBootApplication(scanBasePackages = "com.eldercare")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
