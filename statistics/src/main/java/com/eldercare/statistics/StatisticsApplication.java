package com.eldercare.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 统计报表服务启动类。
 */
@SpringBootApplication(scanBasePackages = "com.eldercare")
@EnableFeignClients(basePackages = "com.eldercare.statistics.feign")
public class StatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StatisticsApplication.class, args);
    }
}
