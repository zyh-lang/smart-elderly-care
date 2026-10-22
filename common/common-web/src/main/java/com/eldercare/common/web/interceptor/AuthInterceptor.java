package com.eldercare.common.web.interceptor;

import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.security.LoginUser;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

/**
 * 鉴权拦截器：
 * 1. 读取网关透传的用户信息（X-User-Id / X-User-Role / X-Username）填充 UserContext；
 * 2. 校验 @RequireRole 标注的方法/类的角色权限。
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        String userId = request.getHeader(AuthConstant.USER_ID_HEADER);
        String role = request.getHeader(AuthConstant.ROLE_HEADER);
        String username = request.getHeader(AuthConstant.USERNAME_HEADER);
        if (StringUtils.hasText(userId)) {
            UserContext.set(new LoginUser(Long.valueOf(userId), role, username));
        }

        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole != null) {
            boolean matched = role != null && Arrays.asList(requireRole.value()).contains(role);
            if (!matched) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
}
