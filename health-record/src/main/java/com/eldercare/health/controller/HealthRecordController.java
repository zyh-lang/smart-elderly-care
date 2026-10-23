package com.eldercare.health.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.health.entity.HealthRecord;
import com.eldercare.health.mapper.HealthRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 健康档案接口。
 */
@RestController
@RequestMapping("/health-record")
public class HealthRecordController {

    @Resource
    private HealthRecordMapper healthRecordMapper;

    /** 查询老人健康档案 */
    @GetMapping("/{elderId}")
    public Result<HealthRecord> get(@PathVariable Long elderId) {
        HealthRecord record = healthRecordMapper.selectOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getElderId, elderId));
        return Result.success(record);
    }

    /** 保存健康档案（存在则更新，不存在则新增） */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody HealthRecord record) {
        HealthRecord exist = healthRecordMapper.selectOne(new LambdaQueryWrapper<HealthRecord>()
                .eq(HealthRecord::getElderId, record.getElderId()));
        if (exist != null) {
            record.setId(exist.getId());
            healthRecordMapper.updateById(record);
        } else {
            healthRecordMapper.insert(record);
        }
        return Result.success();
    }
}
