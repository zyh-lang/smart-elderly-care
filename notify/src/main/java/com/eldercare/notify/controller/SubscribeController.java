package com.eldercare.notify.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.notify.entity.Subscribe;
import com.eldercare.notify.mapper.SubscribeMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 消息订阅接口。
 */
@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    @Resource
    private SubscribeMapper subscribeMapper;

    /** 订阅/退订（body: msgType 消息类型, status 1订阅 0退订） */
    @PostMapping("/toggle")
    public Result<Void> toggle(@RequestBody Subscribe subscribe) {
        Subscribe exist = subscribeMapper.selectOne(new LambdaQueryWrapper<Subscribe>()
                .eq(Subscribe::getUserId, UserContext.getUserId())
                .eq(Subscribe::getMsgType, subscribe.getMsgType()));
        if (exist != null) {
            exist.setStatus(subscribe.getStatus());
            subscribeMapper.updateById(exist);
        } else {
            subscribe.setUserId(UserContext.getUserId());
            subscribeMapper.insert(subscribe);
        }
        return Result.success();
    }

    /** 我的订阅列表 */
    @GetMapping("/my")
    public Result<List<Subscribe>> my() {
        List<Subscribe> list = subscribeMapper.selectList(new LambdaQueryWrapper<Subscribe>()
                .eq(Subscribe::getUserId, UserContext.getUserId()));
        return Result.success(list);
    }
}
