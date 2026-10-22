package com.eldercare.common.security;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.eldercare.common.core.constant.AuthConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 令牌工具：签发、校验、解析载荷。
 * 密钥与有效期通过配置项 jwt.secret / jwt.expire-seconds 注入（秒）。
 */
@Component
public class JwtUtil {

    @Value("${jwt.secret:eldercare-community-2026-secret-key-please-change}")
    private String secret;

    @Value("${jwt.expire-seconds:7200}")
    private long expireSeconds;

    /**
     * 签发令牌。
     */
    public String createToken(Long userId, String role, String username) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(AuthConstant.CLAIM_USER_ID, userId);
        payload.put(AuthConstant.CLAIM_ROLE, role);
        payload.put(AuthConstant.CLAIM_USERNAME, username);
        // hutool JWT 使用标准 exp 声明（秒）
        payload.put("exp", System.currentTimeMillis() / 1000 + expireSeconds);
        return JWTUtil.createToken(payload, secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 校验签名与有效期。
     */
    public boolean verify(String token) {
        try {
            JWTValidator.of(token).validateDate();
            return JWTUtil.verify(token, secret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 解析令牌（调用前应先 verify）。
     */
    public JWT parse(String token) {
        return JWTUtil.parseToken(token);
    }

    public Long getUserId(String token) {
        String v = getString(token, AuthConstant.CLAIM_USER_ID);
        return v == null ? null : Long.valueOf(v);
    }

    public String getRole(String token) {
        return getString(token, AuthConstant.CLAIM_ROLE);
    }

    public String getUsername(String token) {
        return getString(token, AuthConstant.CLAIM_USERNAME);
    }

    private String getString(String token, String key) {
        Object value = parse(token).getPayload(key);
        return value == null ? null : value.toString();
    }
}
