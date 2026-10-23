package com.eldercare.common.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JWT 工具单元测试。
 */
class JwtUtilTest {

    private JwtUtil newJwtUtil() throws Exception {
        JwtUtil util = new JwtUtil();
        Field secretField = JwtUtil.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(util, "unit-test-secret");
        Field expireField = JwtUtil.class.getDeclaredField("expireSeconds");
        expireField.setAccessible(true);
        expireField.set(util, 3600L);
        return util;
    }

    @Test
    @DisplayName("JWT 签发与校验通过")
    void createAndVerify() throws Exception {
        JwtUtil util = newJwtUtil();

        String token = util.createToken(1L, "ADMIN", "admin");

        assertTrue(util.verify(token));
        assertEquals(1L, util.getUserId(token));
        assertEquals("ADMIN", util.getRole(token));
        assertEquals("admin", util.getUsername(token));
    }

    @Test
    @DisplayName("非法令牌校验失败")
    void verifyInvalidToken() throws Exception {
        JwtUtil util = newJwtUtil();

        assertFalse(util.verify("invalid-token-string"));
        assertFalse(util.verify(null));
    }
}
