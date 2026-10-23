package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.core.result.PageResult;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.order.dto.ComplaintDTO;
import com.eldercare.order.entity.Complaint;
import com.eldercare.order.mapper.ComplaintMapper;
import com.eldercare.order.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 投诉接口。
 */
@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    @Resource
    private OrderService orderService;

    @Resource
    private ComplaintMapper complaintMapper;

    @PostMapping("/create")
    @RequireRole({"FAMILY", "ADMIN"})
    public Result<Void> create(@Valid @RequestBody ComplaintDTO dto) {
        orderService.complain(dto);
        return Result.success();
    }

    @GetMapping("/page")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<PageResult<Complaint>> page(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size) {
        Page<Complaint> page = new Page<>(current, size);
        page = complaintMapper.selectPage(page, new LambdaQueryWrapper<Complaint>()
                .orderByDesc(Complaint::getCreateTime));
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }
}
