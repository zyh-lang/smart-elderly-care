package com.eldercare.notify.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.core.dto.SendMessageDTO;
import com.eldercare.common.core.result.PageResult;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.notify.entity.Message;
import com.eldercare.notify.entity.Subscribe;
import com.eldercare.notify.mapper.MessageMapper;
import com.eldercare.notify.mapper.SubscribeMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 站内信接口。
 */
@RestController
@RequestMapping("/message")
public class MessageController {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SubscribeMapper subscribeMapper;

    /**
     * 发送站内信（供其他服务经 OpenFeign 调用）。
     * 若用户已退订该类型消息，则跳过。
     */
    @PostMapping("/send")
    public Result<Void> send(@RequestBody SendMessageDTO dto) {
        Subscribe subscribe = subscribeMapper.selectOne(new LambdaQueryWrapper<Subscribe>()
                .eq(Subscribe::getUserId, dto.getUserId())
                .eq(Subscribe::getMsgType, dto.getMsgType()));
        if (subscribe != null && subscribe.getStatus() != null && subscribe.getStatus() == 0) {
            return Result.success();
        }
        Message message = new Message();
        message.setUserId(dto.getUserId());
        message.setTitle(dto.getTitle());
        message.setContent(dto.getContent());
        message.setMsgType(dto.getMsgType() == null ? "SYSTEM" : dto.getMsgType());
        message.setReadFlag(0);
        messageMapper.insert(message);
        return Result.success();
    }

    /** 我的消息（分页，倒序） */
    @GetMapping("/my")
    public Result<PageResult<Message>> my(@RequestParam(defaultValue = "1") long current,
                                          @RequestParam(defaultValue = "10") long size) {
        Page<Message> page = new Page<>(current, size);
        page = messageMapper.selectPage(page, new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, UserContext.getUserId())
                .orderByDesc(Message::getCreateTime));
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    /** 标记已读 */
    @PutMapping("/read/{id}")
    public Result<Void> read(@PathVariable Long id) {
        Message update = new Message();
        update.setId(id);
        update.setReadFlag(1);
        messageMapper.updateById(update);
        return Result.success();
    }

    /** 未读数量 */
    @GetMapping("/unread-count")
    public Result<Long> unreadCount() {
        Long count = messageMapper.selectCount(new LambdaQueryWrapper<Message>()
                .eq(Message::getUserId, UserContext.getUserId())
                .eq(Message::getReadFlag, 0));
        return Result.success(count);
    }
}
