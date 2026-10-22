package com.eldercare.gateway.filter;

import cn.hutool.json.JSONUtil;
import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.security.JwtUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局 JWT 鉴权过滤器：
 * 1. 白名单路径直接放行（登录、注册）；
 * 2. 校验 Authorization 中的 JWT（签名 + 有效期）；
 * 3. 校验 Redis 会话（支持登出失效）；
 * 4. 校验通过后向下游透传用户信息（X-User-Id / X-User-Role / X-Username）。
 */
@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    /** 无需登录即可访问的网关路径 */
    private static final List<String> WHITE_LIST = List.of(
            "/api/user/auth/login",
            "/api/user/auth/register"
    );

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        if (isWhiteListed(path)) {
            return chain.filter(exchange);
        }

        String token = extractToken(request);
        if (!StringUtils.hasText(token) || !jwtUtil.verify(token)) {
            return unauthorized(exchange, "未登录或令牌无效");
        }

        Long userId = jwtUtil.getUserId(token);
        if (userId == null) {
            return unauthorized(exchange, "令牌缺少用户信息");
        }
        String role = jwtUtil.getRole(token);
        String username = jwtUtil.getUsername(token);

        // 校验 Redis 会话：login:token:{userId} 的值必须等于当前 token
        String sessionKey = AuthConstant.REDIS_LOGIN_PREFIX + userId;
        return reactiveStringRedisTemplate.opsForValue().get(sessionKey)
                .flatMap(stored -> {
                    if (token.equals(stored)) {
                        ServerHttpRequest mutated = request.mutate()
                                .header(AuthConstant.USER_ID_HEADER, String.valueOf(userId))
                                .header(AuthConstant.ROLE_HEADER, role)
                                .header(AuthConstant.USERNAME_HEADER, username)
                                .build();
                        return chain.filter(exchange.mutate().request(mutated).build());
                    }
                    return unauthorized(exchange, "登录已失效，请重新登录");
                })
                .switchIfEmpty(unauthorized(exchange, "登录已失效，请重新登录"));
    }

    private boolean isWhiteListed(String path) {
        return WHITE_LIST.stream().anyMatch(path::equals);
    }

    private String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(AuthConstant.TOKEN_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(AuthConstant.TOKEN_PREFIX)) {
            return header.substring(AuthConstant.TOKEN_PREFIX.length()).trim();
        }
        return null;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = JSONUtil.toJsonStr(Result.fail(ErrorCode.UNAUTHORIZED, message));
        DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
