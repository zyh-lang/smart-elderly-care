package com.eldercare.user.controller;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.core.result.PageResult;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.redis.util.RedisUtil;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.user.entity.SysUser;
import com.eldercare.user.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口：当前用户信息、管理员用户管理、修改密码。
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private RedisUtil redisUtil;

    /** 当前登录用户信息 */
    @GetMapping("/info")
    public Result<SysUser> info() {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        user.setPassword(null);
        return Result.success(user);
    }

    /** 用户分页列表（仅管理员） */
    @GetMapping("/list")
    @RequireRole("ADMIN")
    public Result<PageResult<SysUser>> list(@RequestParam(defaultValue = "1") long current,
                                            @RequestParam(defaultValue = "10") long size,
                                            @RequestParam(required = false) String keyword) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreateTime);
        page = sysUserMapper.selectPage(page, wrapper);
        page.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    /** 启用/禁用用户（仅管理员） */
    @PutMapping("/status")
    @RequireRole("ADMIN")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserMapper.updateById(user);
        return Result.success();
    }

    /** 修改密码（改后强制重新登录） */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null || !BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new BusinessException(ErrorCode.OLD_PASSWORD_ERROR);
        }
        SysUser update = new SysUser();
        update.setId(userId);
        update.setPassword(BCrypt.hashpw(newPassword));
        sysUserMapper.updateById(update);
        // 使旧会话失效，强制重新登录
        redisUtil.delete(AuthConstant.REDIS_LOGIN_PREFIX + userId);
        return Result.success();
    }
}
