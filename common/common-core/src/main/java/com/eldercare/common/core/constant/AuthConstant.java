package com.eldercare.common.core.constant;

/**
 * 认证与鉴权相关常量。
 */
public interface AuthConstant {

    /** 请求头：登录令牌 */
    String TOKEN_HEADER = "Authorization";

    /** 令牌前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** 网关解析后向下游传递的用户信息请求头 */
    String USER_ID_HEADER = "X-User-Id";
    String ROLE_HEADER = "X-User-Role";
    String USERNAME_HEADER = "X-Username";

    /** 服务间内部调用令牌请求头 */
    String INTERNAL_TOKEN_HEADER = "X-Internal-Token";

    /** Redis 登录会话键前缀，完整键为 login:token:{userId} */
    String REDIS_LOGIN_PREFIX = "login:token:";

    /** JWT 载荷键 */
    String CLAIM_USER_ID = "userId";
    String CLAIM_ROLE = "role";
    String CLAIM_USERNAME = "username";

    /** 角色编码 */
    String ROLE_ADMIN = "ADMIN";
    String ROLE_STAFF = "STAFF";
    String ROLE_PROVIDER = "PROVIDER";
    String ROLE_FAMILY = "FAMILY";
}
