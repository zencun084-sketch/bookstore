package com.bookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {

    /**
     * 分页查询订单(含用户昵称)
     */
    IPage<Orders> selectOrderPage(Page<Orders> page, @Param("status") Integer status,
                                  @Param("orderNo") String orderNo);

    /**
     * 今日销售额
     */
    BigDecimal selectTodaySales();

    /**
     * 今日订单数
     */
    Integer selectTodayOrderCount();

    /**
     * 近7天订单趋势
     */
    List<Map<String, Object>> selectOrderTrend(@Param("days") int days);

    /**
     * 近7天销售趋势
     */
    List<Map<String, Object>> selectSalesTrend(@Param("days") int days);

    /**
     * 热门图书销量排行
     */
    List<Map<String, Object>> selectHotBooksRank(@Param("limit") int limit);
}
