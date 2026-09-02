package com.bookstore.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.security.UserContext;
import com.bookstore.service.DashboardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据统计控制器
 */
@Api(tags = "数据统计接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @ApiOperation("概览数据")
    @GetMapping("/overview")
    public Result<Map<String, Object>> overview() {
        checkAdmin();
        return Result.success(dashboardService.overview());
    }

    @ApiOperation("订单趋势")
    @GetMapping("/order-trend")
    public Result<Map<String, Object>> orderTrend(@RequestParam(defaultValue = "7") Integer days) {
        checkAdmin();
        return Result.success(dashboardService.orderTrend(days));
    }

    @ApiOperation("销售趋势")
    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> salesTrend(@RequestParam(defaultValue = "7") Integer days) {
        checkAdmin();
        return Result.success(dashboardService.salesTrend(days));
    }

    @ApiOperation("热门图书排行")
    @GetMapping("/hot-books")
    public Result<Map<String, Object>> hotBooks(@RequestParam(defaultValue = "10") Integer limit) {
        checkAdmin();
        return Result.success(dashboardService.hotBooksRank(limit));
    }

    @ApiOperation("分类销量分布")
    @GetMapping("/category-sales")
    public Result<Map<String, Object>> categorySales() {
        checkAdmin();
        return Result.success(dashboardService.categorySales());
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
