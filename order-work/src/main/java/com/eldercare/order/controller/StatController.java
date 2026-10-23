package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.core.vo.StatServiceRankVO;
import com.eldercare.common.core.vo.StatSummaryVO;
import com.eldercare.common.core.vo.StatTrendVO;
import com.eldercare.order.entity.Evaluation;
import com.eldercare.order.entity.ServiceOrder;
import com.eldercare.order.entity.WorkOrder;
import com.eldercare.order.enums.WorkOrderStatus;
import com.eldercare.order.mapper.ComplaintMapper;
import com.eldercare.order.mapper.EvaluationMapper;
import com.eldercare.order.mapper.ServiceOrderMapper;
import com.eldercare.order.mapper.WorkOrderMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 统计接口（数据源，供 statistics 服务经 OpenFeign 调用）。
 */
@RestController
@RequestMapping("/stat")
public class StatController {

    @Resource
    private ServiceOrderMapper serviceOrderMapper;

    @Resource
    private WorkOrderMapper workOrderMapper;

    @Resource
    private EvaluationMapper evaluationMapper;

    @Resource
    private ComplaintMapper complaintMapper;

    /** 汇总统计 */
    @GetMapping("/summary")
    public Result<StatSummaryVO> summary() {
        StatSummaryVO vo = new StatSummaryVO();
        vo.setOrderCount(serviceOrderMapper.selectCount(null));
        vo.setWorkOrderCount(workOrderMapper.selectCount(null));

        List<ServiceOrder> orders = serviceOrderMapper.selectList(null);
        BigDecimal totalAmount = orders.stream()
                .map(ServiceOrder::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setTotalAmount(totalAmount);

        vo.setInServiceCount(workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .in(WorkOrder::getStatus, WorkOrderStatus.DISPATCHED,
                        WorkOrderStatus.IN_SERVICE, WorkOrderStatus.WAIT_ACCEPT)));
        vo.setCompletedCount(workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .in(WorkOrder::getStatus, WorkOrderStatus.COMPLETED, WorkOrderStatus.EVALUATED)));
        vo.setCancelledCount(workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getStatus, WorkOrderStatus.CANCELLED)));
        vo.setComplainedCount(workOrderMapper.selectCount(new LambdaQueryWrapper<WorkOrder>()
                .eq(WorkOrder::getStatus, WorkOrderStatus.COMPLAINED)));

        List<Evaluation> evaluations = evaluationMapper.selectList(null);
        double avg = evaluations.stream().mapToInt(Evaluation::getRating).average().orElse(0.0);
        vo.setAvgRating(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));

        vo.setComplaintCount(complaintMapper.selectCount(null));
        return Result.success(vo);
    }

    /** 近 N 天日趋势（订单数与营收） */
    @GetMapping("/trend")
    public Result<List<StatTrendVO>> trend(@RequestParam(defaultValue = "7") int days) {
        int n = Math.min(Math.max(days, 1), 90);
        LocalDate start = LocalDate.now().minusDays(n - 1L);
        List<ServiceOrder> orders = serviceOrderMapper.selectList(new LambdaQueryWrapper<ServiceOrder>()
                .ge(ServiceOrder::getCreateTime, start.atStartOfDay()));
        Map<LocalDate, List<ServiceOrder>> grouped = orders.stream()
                .collect(Collectors.groupingBy(o -> o.getCreateTime().toLocalDate()));
        List<StatTrendVO> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            LocalDate d = start.plusDays(i);
            List<ServiceOrder> dayOrders = grouped.getOrDefault(d, Collections.emptyList());
            StatTrendVO vo = new StatTrendVO();
            vo.setDate(d.toString());
            vo.setOrderCount((long) dayOrders.size());
            vo.setAmount(dayOrders.stream().map(ServiceOrder::getAmount)
                    .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            result.add(vo);
        }
        return Result.success(result);
    }

    /** 服务类型排名（按订单数） */
    @GetMapping("/service-rank")
    public Result<List<StatServiceRankVO>> serviceRank() {
        List<ServiceOrder> orders = serviceOrderMapper.selectList(null);
        Map<String, List<ServiceOrder>> grouped = orders.stream()
                .filter(o -> o.getServiceType() != null)
                .collect(Collectors.groupingBy(ServiceOrder::getServiceType));
        List<StatServiceRankVO> result = grouped.entrySet().stream().map(e -> {
            StatServiceRankVO vo = new StatServiceRankVO();
            vo.setServiceType(e.getKey());
            vo.setOrderCount((long) e.getValue().size());
            vo.setAmount(e.getValue().stream().map(ServiceOrder::getAmount)
                    .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add));
            return vo;
        }).sorted(Comparator.comparing(StatServiceRankVO::getOrderCount).reversed())
                .collect(Collectors.toList());
        return Result.success(result);
    }
}
