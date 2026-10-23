package com.eldercare.statistics.controller;

import com.eldercare.common.core.result.Result;
import com.eldercare.common.core.vo.StatServiceRankVO;
import com.eldercare.common.core.vo.StatSummaryVO;
import com.eldercare.common.core.vo.StatTrendVO;
import com.eldercare.common.redis.util.RedisUtil;
import com.eldercare.statistics.feign.OrderStatClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 统计报表接口：经 OpenFeign 聚合 order-work 数据，Redis 缓存 30 秒。
 */
@RestController
@RequestMapping("/report")
public class StatisticsController {

    /** 报表缓存时间（秒） */
    private static final long CACHE_SECONDS = 30;

    @Resource
    private OrderStatClient orderStatClient;

    @Resource
    private RedisUtil redisUtil;

    @GetMapping("/summary")
    public Result<StatSummaryVO> summary() {
        String key = "report:summary";
        StatSummaryVO cached = redisUtil.getObject(key);
        if (cached != null) {
            return Result.success(cached);
        }
        Result<StatSummaryVO> result = orderStatClient.summary();
        if (!result.isSuccess()) {
            return Result.fail(result.getCode(), result.getMessage());
        }
        redisUtil.setObject(key, result.getData(), CACHE_SECONDS);
        return Result.success(result.getData());
    }

    @GetMapping("/trend")
    public Result<List<StatTrendVO>> trend(@RequestParam(defaultValue = "7") int days) {
        String key = "report:trend:" + days;
        List<StatTrendVO> cached = redisUtil.getObject(key);
        if (cached != null) {
            return Result.success(cached);
        }
        Result<List<StatTrendVO>> result = orderStatClient.trend(days);
        if (!result.isSuccess()) {
            return Result.fail(result.getCode(), result.getMessage());
        }
        redisUtil.setObject(key, result.getData(), CACHE_SECONDS);
        return Result.success(result.getData());
    }

    @GetMapping("/service-rank")
    public Result<List<StatServiceRankVO>> serviceRank() {
        String key = "report:service-rank";
        List<StatServiceRankVO> cached = redisUtil.getObject(key);
        if (cached != null) {
            return Result.success(cached);
        }
        Result<List<StatServiceRankVO>> result = orderStatClient.serviceRank();
        if (!result.isSuccess()) {
            return Result.fail(result.getCode(), result.getMessage());
        }
        redisUtil.setObject(key, result.getData(), CACHE_SECONDS);
        return Result.success(result.getData());
    }
}
