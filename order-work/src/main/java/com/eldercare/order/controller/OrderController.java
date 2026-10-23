package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.eldercare.common.core.result.PageResult;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.common.web.context.UserContext;
import com.eldercare.order.dto.OrderCreateDTO;
import com.eldercare.order.entity.ServiceOrder;
import com.eldercare.order.entity.WorkOrder;
import com.eldercare.order.mapper.ServiceOrderMapper;
import com.eldercare.order.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单接口：下单、派单、查询。
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    /** 家属下单 */
    @PostMapping("/create")
    @RequireRole({"FAMILY", "ADMIN"})
    public Result<Long> create(@Valid @RequestBody OrderCreateDTO dto) {
        return Result.success(orderService.createOrder(dto));
    }

    /** 智能派单（管理员/工作人员） */
    @PostMapping("/dispatch/{orderId}")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<WorkOrder> dispatch(@PathVariable Long orderId) {
        return Result.success(orderService.dispatch(orderId));
    }

    /** 订单分页（管理员/工作人员） */
    @GetMapping("/page")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<PageResult<ServiceOrder>> page(@RequestParam(defaultValue = "1") long current,
                                                 @RequestParam(defaultValue = "10") long size) {
        Page<ServiceOrder> page = new Page<>(current, size);
        page = serviceOrderMapper.selectPage(page, new LambdaQueryWrapper<ServiceOrder>()
                .orderByDesc(ServiceOrder::getCreateTime));
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    /** 我的订单（家属） */
    @GetMapping("/my")
    public Result<List<ServiceOrder>> my() {
        List<ServiceOrder> list = serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>()
                .eq(ServiceOrder::getFamilyUserId, UserContext.getUserId())
                .orderByDesc(ServiceOrder::getCreateTime));
        return Result.success(list);
    }
}
