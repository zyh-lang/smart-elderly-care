package com.eldercare.statistics.feign;

import com.eldercare.common.core.result.Result;
import com.eldercare.common.core.vo.StatServiceRankVO;
import com.eldercare.common.core.vo.StatSummaryVO;
import com.eldercare.common.core.vo.StatTrendVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 服务与工单服务统计 Feign 客户端。
 */
@FeignClient(name = "order-work")
public interface OrderStatClient {

    @GetMapping("/stat/summary")
    Result<StatSummaryVO> summary();

    @GetMapping("/stat/trend")
    Result<List<StatTrendVO>> trend(@RequestParam("days") int days);

    @GetMapping("/stat/service-rank")
    Result<List<StatServiceRankVO>> serviceRank();
}
