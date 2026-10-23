package com.eldercare.health;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 健康档案服务启动类。
 */
@SpringBootApplication(scanBasePackages = "com.eldercare")
@MapperScan("com.eldercare.health.mapper")
public class HealthRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthRecordApplication.class, args);
    }
}
