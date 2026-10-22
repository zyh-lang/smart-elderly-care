package com.eldercare.common.redis.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

/**
 * 基于 Redis SETNX + Lua 的简易分布式锁。
 * 用于智能派单、下单幂等等并发场景。
 */
@Component
public class DistributedLock {

    private static final String LOCK_PREFIX = "lock:";

    /** 释放锁的 Lua 脚本：仅当值匹配时才删除，避免误删他人锁 */
    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT = new DefaultRedisScript<>(
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end",
            Long.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 尝试加锁。
     *
     * @param key           业务键
     * @param value         持有者标识（一般为唯一请求号）
     * @param expireSeconds 锁过期时间（秒）
     */
    public boolean tryLock(String key, String value, long expireSeconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue()
                .setIfAbsent(LOCK_PREFIX + key, value, Duration.ofSeconds(expireSeconds)));
    }

    /**
     * 释放锁。
     */
    public void unlock(String key, String value) {
        stringRedisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(LOCK_PREFIX + key), value);
    }
}
