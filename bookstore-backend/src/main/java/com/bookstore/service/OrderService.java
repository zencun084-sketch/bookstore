package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.dto.CreateOrderDTO;
import com.bookstore.entity.Orders;

public interface OrderService extends IService<Orders> {

    /**
     * 创建订单(从购物车选中项)
     */
    Orders createOrder(CreateOrderDTO dto);

    /**
     * 模拟支付
     */
    void payOrder(Long orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmOrder(Long orderId);

    /**
     * 发货(管理员)
     */
    void shipOrder(Long orderId);

    /**
     * 我的订单(分页)
     */
    IPage<Orders> myOrders(Integer current, Integer size, Integer status);

    /**
     * 后台订单分页
     */
    IPage<Orders> adminOrders(Integer current, Integer size, Integer status, String orderNo);

    /**
     * 订单详情
     */
    Orders orderDetail(Long orderId);

    /**
     * 再次购买(将订单商品加入购物车)
     */
    void repurchase(Long orderId);
}
