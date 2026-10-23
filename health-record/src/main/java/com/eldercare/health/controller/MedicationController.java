package com.eldercare.health.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.health.entity.Medication;
import com.eldercare.health.mapper.MedicationMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用药记录接口。
 */
@RestController
@RequestMapping("/medication")
public class MedicationController {

    @Resource
    private MedicationMapper medicationMapper;

    @PostMapping("/add")
    public Result<Void> add(@RequestBody Medication medication) {
        medicationMapper.insert(medication);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Medication>> list(@RequestParam Long elderId) {
        List<Medication> list = medicationMapper.selectList(new LambdaQueryWrapper<Medication>()
                .eq(Medication::getElderId, elderId)
                .orderByDesc(Medication::getCreateTime));
        return Result.success(list);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody Medication medication) {
        medicationMapper.updateById(medication);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        medicationMapper.deleteById(id);
        return Result.success();
    }
}
