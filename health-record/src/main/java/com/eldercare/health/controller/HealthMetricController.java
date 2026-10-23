package com.eldercare.health.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.health.entity.HealthMetric;
import com.eldercare.health.mapper.HealthMetricMapper;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康指标接口（血压/血糖/心率等时序数据与趋势）。
 */
@RestController
@RequestMapping("/health-metric")
public class HealthMetricController {

    @Resource
    private HealthMetricMapper healthMetricMapper;

    /** 录入指标 */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody HealthMetric metric) {
        if (metric.getMeasureTime() == null) {
            metric.setMeasureTime(LocalDateTime.now());
        }
        healthMetricMapper.insert(metric);
        return Result.success();
    }

    /** 指标列表（可按类型过滤，倒序） */
    @GetMapping("/list")
    public Result<List<HealthMetric>> list(@RequestParam Long elderId,
                                           @RequestParam(required = false) String metricType) {
        LambdaQueryWrapper<HealthMetric> wrapper = new LambdaQueryWrapper<HealthMetric>()
                .eq(HealthMetric::getElderId, elderId);
        if (StringUtils.hasText(metricType)) {
            wrapper.eq(HealthMetric::getMetricType, metricType);
        }
        wrapper.orderByDesc(HealthMetric::getMeasureTime);
        return Result.success(healthMetricMapper.selectList(wrapper));
    }

    /** 指标趋势（最近 N 条，升序，供图表展示） */
    @GetMapping("/trend")
    public Result<List<HealthMetric>> trend(@RequestParam Long elderId,
                                            @RequestParam String metricType,
                                            @RequestParam(defaultValue = "30") long limit) {
        List<HealthMetric> list = healthMetricMapper.selectList(new LambdaQueryWrapper<HealthMetric>()
                .eq(HealthMetric::getElderId, elderId)
                .eq(HealthMetric::getMetricType, metricType)
                .orderByDesc(HealthMetric::getMeasureTime)
                .last("LIMIT " + Math.min(Math.max(limit, 1), 100)));
        java.util.Collections.reverse(list);
        return Result.success(list);
    }
}
