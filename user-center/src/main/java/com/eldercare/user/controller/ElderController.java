package com.eldercare.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.user.entity.Elder;
import com.eldercare.user.entity.FamilyBinding;
import com.eldercare.user.mapper.ElderMapper;
import com.eldercare.user.mapper.FamilyBindingMapper;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    /** 新增老人（管理员/工作人员），可选绑定家属账号 */
    @PostMapping("/add")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Long> add(@RequestBody Elder elder) {
        if (!StringUtils.hasText(elder.getName())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "老人姓名不能为空");
        }
        elder.setId(null);
        elderMapper.insert(elder);
        bindFamily(elder);
        return Result.success(elder.getId());
    }

    /** 修改老人（管理员/工作人员），同步更新家属绑定 */
    @PutMapping("/update")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Void> update(@RequestBody Elder elder) {
        elderMapper.updateById(elder);
        // 重建绑定关系：先清理旧绑定，再按新家属绑定
        familyBindingMapper.delete(new LambdaQueryWrapper<FamilyBinding>()
                .eq(FamilyBinding::getElderId, elder.getId()));
        bindFamily(elder);
        return Result.success();
    }

    /** 按 familyUserId 建立家属-老人绑定 */
    private void bindFamily(Elder elder) {
        if (elder.getFamilyUserId() == null) {
            return;
        }
        FamilyBinding binding = new FamilyBinding();
        binding.setFamilyUserId(elder.getFamilyUserId());
        binding.setElderId(elder.getId());
        binding.setRelation(StringUtils.hasText(elder.getRelation()) ? elder.getRelation() : "子女");
        familyBindingMapper.insert(binding);
    }

    /** 删除老人（管理员/工作人员，逻辑删除并清理绑定关系） */
    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Void> delete(@PathVariable Long id) {
        elderMapper.deleteById(id);
        familyBindingMapper.delete(new LambdaQueryWrapper<FamilyBinding>()
                .eq(FamilyBinding::getElderId, id));
        return Result.success();
    }
}
