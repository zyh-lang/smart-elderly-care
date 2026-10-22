package com.eldercare.common.redis.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存工具：封装常用操作。
 */
@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /* ---------- 通用 Key ---------- */

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    public boolean expire(String key, long seconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS));
    }

    /* ---------- 字符串 ---------- */

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, String value, long seconds) {
        stringRedisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean setIfAbsent(String key, String value, long seconds) {
        return Boolean.TRUE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS));
    }

    /* ---------- 对象 ---------- */

    public void setObject(String key, Object value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key) {
        return (T) redisTemplate.opsForValue().get(key);
    }

    /* ---------- 计数 ---------- */

    public long increment(String key) {
        Long value = stringRedisTemplate.opsForValue().increment(key);
        return value == null ? 0L : value;
    }

    public long decrement(String key) {
        Long value = stringRedisTemplate.opsForValue().decrement(key);
        return value == null ? 0L : value;
    }
}
