package com.bookstore.service;

import java.util.Map;

public interface DashboardService {

    /**
     * 概览数据(今日订单/销售额/新增用户/总用户/总图书)
     */
    Map<String, Object> overview();

    /**
     * 近N天订单趋势
     */
    Map<String, Object> orderTrend(int days);

    /**
     * 近N天销售趋势
     */
    Map<String, Object> salesTrend(int days);

    /**
     * 热门图书排行
     */
    Map<String, Object> hotBooksRank(int limit);

    /**
     * 分类销量分布
     */
    Map<String, Object> categorySales();
}
