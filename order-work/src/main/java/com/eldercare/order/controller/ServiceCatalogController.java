package com.eldercare.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.eldercare.common.core.constant.CommonConstant;
import com.eldercare.common.core.result.Result;
import com.eldercare.common.web.annotation.RequireRole;
import com.eldercare.order.entity.ServiceCatalog;
import com.eldercare.order.mapper.ServiceCatalogMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务目录接口。
 */
@RestController
@RequestMapping("/service")
public class ServiceCatalogController {

    @Resource
    private ServiceCatalogMapper serviceCatalogMapper;

    @GetMapping("/list")
    public Result<List<ServiceCatalog>> list() {
        List<ServiceCatalog> list = serviceCatalogMapper.selectList(new LambdaQueryWrapper<ServiceCatalog>()
                .eq(ServiceCatalog::getStatus, CommonConstant.STATUS_ENABLED)
                .orderByAsc(ServiceCatalog::getId));
        return Result.success(list);
    }

    @PostMapping("/add")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Void> add(@RequestBody ServiceCatalog catalog) {
        serviceCatalogMapper.insert(catalog);
        return Result.success();
    }

    @PutMapping("/update")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Void> update(@RequestBody ServiceCatalog catalog) {
        serviceCatalogMapper.updateById(catalog);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "STAFF"})
    public Result<Void> delete(@PathVariable Long id) {
        serviceCatalogMapper.deleteById(id);
        return Result.success();
    }
}
