package com.eldercare.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.constant.AuthConstant;
import com.eldercare.common.core.constant.CommonConstant;
import com.eldercare.common.core.exception.BusinessException;
import com.eldercare.common.core.exception.ErrorCode;
import com.eldercare.common.redis.util.RedisUtil;
import com.eldercare.common.security.JwtUtil;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.user.dto.LoginDTO;
import com.eldercare.user.dto.LoginVO;
import com.eldercare.user.dto.RegisterDTO;
import com.eldercare.user.entity.SysUser;
import com.eldercare.user.mapper.SysUserMapper;
import com.eldercare.user.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现：登录、注册、登出。
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisUtil redisUtil;

    @Value("${jwt.expire-seconds:7200}")
    private long expireSeconds;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (user == null || !BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        if (CommonConstant.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 签发 JWT 并写入 Redis 会话（供网关鉴权校验）
        String token = jwtUtil.createToken(user.getId(), user.getRole(), user.getUsername());
        redisUtil.set(AuthConstant.REDIS_LOGIN_PREFIX + user.getId(), token, expireSeconds);

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRole(user.getRole());
        vo.setRealName(user.getRealName());
        return vo;
    }

    @Override
    public void register(RegisterDTO dto) {
        Long count = sysUserMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.USERNAME_EXIST);
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setRealName(dto.getRealName());
        user.setStatus(CommonConstant.STATUS_ENABLED);
        sysUserMapper.insert(user);
    }

    @Override
    public void logout() {
        Long userId = UserContext.getUserId();
        if (userId != null) {
            redisUtil.delete(AuthConstant.REDIS_LOGIN_PREFIX + userId);
        }
    }
}
