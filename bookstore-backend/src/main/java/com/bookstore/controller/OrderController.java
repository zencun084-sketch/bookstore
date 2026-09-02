package com.bookstore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.common.Result;
import com.bookstore.dto.CreateOrderDTO;
import com.bookstore.entity.Orders;
import com.bookstore.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 订单控制器
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @ApiOperation("创建订单")
    @PostMapping
    public Result<Orders> create(@Valid @RequestBody CreateOrderDTO dto) {
        return Result.success(orderService.createOrder(dto));
    }

    @ApiOperation("我的订单(分页)")
    @GetMapping("/my")
    public Result<IPage<Orders>> myOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        return Result.success(orderService.myOrders(current, size, status));
    }

    @ApiOperation("订单详情")
    @GetMapping("/{id}")
    public Result<Orders> detail(@PathVariable Long id) {
        return Result.success(orderService.orderDetail(id));
    }

    @ApiOperation("模拟支付")
    @PutMapping("/{id}/pay")
    public Result<Void> pay(@PathVariable Long id) {
        orderService.payOrder(id);
        return Result.success();
    }

    @ApiOperation("取消订单")
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success();
    }

    @ApiOperation("确认收货")
    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        orderService.confirmOrder(id);
        return Result.success();
    }

    @ApiOperation("再次购买")
    @PostMapping("/{id}/repurchase")
    public Result<Void> repurchase(@PathVariable Long id) {
        orderService.repurchase(id);
        return Result.success();
    }

    // ============ 后台管理 ============

    @ApiOperation("订单分页(管理员)")
    @GetMapping("/page")
    public Result<IPage<Orders>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo) {
        return Result.success(orderService.adminOrders(current, size, status, orderNo));
    }

    @ApiOperation("发货(管理员)")
    @PutMapping("/{id}/ship")
    public Result<Void> ship(@PathVariable Long id) {
        orderService.shipOrder(id);
        return Result.success();
    }
}
