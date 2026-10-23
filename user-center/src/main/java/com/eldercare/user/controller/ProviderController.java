package com.eldercare.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.constant.CommonConstant;
import com.eldercare.common.core.result.Result;
import com.eldercare.user.entity.Provider;
import com.eldercare.user.mapper.ProviderMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务商接口（供 order-work 通过 OpenFeign 调用，做智能派单）。
 */
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Resource
    private ProviderMapper providerMapper;

    /** 服务商列表（按评分降序） */
    @GetMapping("/list")
    public Result<List<Provider>> list() {
        List<Provider> list = providerMapper.selectList(new LambdaQueryWrapper<Provider>()
                .eq(Provider::getStatus, CommonConstant.STATUS_ENABLED)
                .orderByDesc(Provider::getRating));
        return Result.success(list);
    }
}
