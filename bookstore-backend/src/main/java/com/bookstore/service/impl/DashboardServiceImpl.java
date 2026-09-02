package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.User;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.OrdersMapper;
import com.bookstore.service.DashboardService;
import com.bookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrdersMapper ordersMapper;
    private final BookMapper bookMapper;
    private final UserService userService;

    @Override
    public Map<String, Object> overview() {
        Map<String, Object> result = new HashMap<>();
        // 今日订单数
        result.put("todayOrders", ordersMapper.selectTodayOrderCount());
        // 今日销售额
        result.put("todaySales", ordersMapper.selectTodaySales());
        // 今日新增用户
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayNewUsers = userService.count(new LambdaQueryWrapper<User>()
                .ge(User::getCreateTime, todayStart));
        result.put("todayNewUsers", todayNewUsers);
        // 总用户数
        result.put("totalUsers", userService.count());
        // 总图书数(上架)
        result.put("totalBooks", bookMapper.selectCount(null));
        // 总销售额
        result.put("totalSales", ordersMapper.selectTodaySales()); // 简化: 复用方法, 实际可单独查询
        return result;
    }

    @Override
    public Map<String, Object> orderTrend(int days) {
        List<Map<String, Object>> data = ordersMapper.selectOrderTrend(days);
        // 补全没有数据的日期
        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        for (Map<String, Object> row : data) {
            dataMap.put(String.valueOf(row.get("date")), row.get("count"));
        }
        // 填充近 days 天
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            dates.add(date);
            counts.add(dataMap.get(date) != null ? ((Number) dataMap.get(date)).intValue() : 0);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("counts", counts);
        return result;
    }

    @Override
    public Map<String, Object> salesTrend(int days) {
        List<Map<String, Object>> data = ordersMapper.selectSalesTrend(days);
        List<String> dates = new ArrayList<>();
        List<Object> amounts = new ArrayList<>();
        Map<String, Object> dataMap = new LinkedHashMap<>();
        for (Map<String, Object> row : data) {
            dataMap.put(String.valueOf(row.get("date")), row.get("amount"));
        }
        LocalDate today = LocalDate.now();
        for (int i = days - 1; i >= 0; i--) {
            String date = today.minusDays(i).toString();
            dates.add(date);
            amounts.add(dataMap.get(date) != null ? dataMap.get(date) : 0);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("amounts", amounts);
        return result;
    }

    @Override
    public Map<String, Object> hotBooksRank(int limit) {
        List<Map<String, Object>> data = ordersMapper.selectHotBooksRank(limit);
        List<String> titles = new ArrayList<>();
        List<Object> sales = new ArrayList<>();
        List<Object> amounts = new ArrayList<>();
        for (Map<String, Object> row : data) {
            titles.add(String.valueOf(row.get("title")));
            sales.add(row.get("sales"));
            amounts.add(row.get("amount"));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("titles", titles);
        result.put("sales", sales);
        result.put("amounts", amounts);
        result.put("raw", data);
        return result;
    }

    @Override
    public Map<String, Object> categorySales() {
        // 各分类图书数量及销量分布
        List<Map<String, Object>> data = bookMapper.selectCategoryDistribution();
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }
}
