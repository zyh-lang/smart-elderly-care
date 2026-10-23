package com.eldercare.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.user.entity.Elder;
import com.eldercare.user.entity.FamilyBinding;
import com.eldercare.user.mapper.ElderMapper;
import com.eldercare.user.mapper.FamilyBindingMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 老人档案接口。
 */
@RestController
@RequestMapping("/elder")
public class ElderController {

    @Resource
    private ElderMapper elderMapper;

    @Resource
    private FamilyBindingMapper familyBindingMapper;

    /** 当前家属绑定的老人列表 */
    @GetMapping("/my")
    public Result<List<Elder>> my() {
        List<FamilyBinding> bindings = familyBindingMapper.selectList(new LambdaQueryWrapper<FamilyBinding>()
                .eq(FamilyBinding::getFamilyUserId, UserContext.getUserId()));
        List<Long> elderIds = bindings.stream().map(FamilyBinding::getElderId).collect(Collectors.toList());
        if (elderIds.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<Elder> elders = elderMapper.selectList(new LambdaQueryWrapper<Elder>()
                .in(Elder::getId, elderIds)
                .orderByAsc(Elder::getId));
        return Result.success(elders);
    }

    /** 全部老人列表（管理员/工作人员，管理端健康档案用） */
    @GetMapping("/list")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<List<Elder>> list() {
        List<Elder> elders = elderMapper.selectList(new LambdaQueryWrapper<Elder>()
                .orderByAsc(Elder::getId));
        return Result.success(elders);
    }
}
