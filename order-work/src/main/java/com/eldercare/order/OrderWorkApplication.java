package com.eldercare.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 服务与工单服务启动类。
 */
@SpringBootApplication(scanBasePackages = "com.eldercare")
@MapperScan("com.eldercare.order.mapper")
@EnableFeignClients(basePackages = "com.eldercare.order.feign")
public class OrderWorkApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderWorkApplication.class, args);
    }
}
