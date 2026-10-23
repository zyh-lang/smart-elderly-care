package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.order.dto.EvaluationDTO;
import com.eldercare.order.entity.Evaluation;
import com.eldercare.order.mapper.EvaluationMapper;
import com.eldercare.order.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 评价接口。
 */
@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

    @Resource
    private OrderService orderService;

    @Resource
    private EvaluationMapper evaluationMapper;

    @PostMapping("/create")
    @RequireRole({"FAMILY", "ADMIN"})
    public Result<Void> create(@Valid @RequestBody EvaluationDTO dto) {
        orderService.evaluate(dto);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Evaluation>> list(@RequestParam Long workOrderId) {
        List<Evaluation> list = evaluationMapper.selectList(new LambdaQueryWrapper<Evaluation>()
                .eq(Evaluation::getWorkOrderId, workOrderId)
                .orderByDesc(Evaluation::getCreateTime));
        return Result.success(list);
    }
}
