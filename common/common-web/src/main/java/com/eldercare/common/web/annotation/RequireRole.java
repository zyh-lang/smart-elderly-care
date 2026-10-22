package com.eldercare.common.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色访问控制注解，标注在 Controller 类或方法上。
 * 由 AuthInterceptor 校验当前登录用户角色是否在允许列表中。
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 允许访问的角色编码，如 {"ADMIN", "STAFF"}。
     */
    String[] value();
}
