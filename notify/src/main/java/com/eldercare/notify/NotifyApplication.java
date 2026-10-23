package com.eldercare.notify;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 消息通知服务启动类。
 */
@SpringBootApplication(scanBasePackages = "com.eldercare")
@MapperScan("com.eldercare.notify.mapper")
public class NotifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifyApplication.class, args);
    }
}
