package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.entity.CartItem;
import com.bookstore.service.CartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 */
@Api(tags = "购物车接口")
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @ApiOperation("查看购物车")
    @GetMapping
    public Result<List<CartItem>> list() {
        return Result.success(cartService.getMyCart());
    }

    @ApiOperation("加入购物车")
    @PostMapping("/items")
    public Result<Void> add(@RequestParam Long bookId, @RequestParam(defaultValue = "1") Integer quantity) {
        cartService.addToCart(bookId, quantity);
        return Result.success();
    }

    @ApiOperation("修改数量")
    @PutMapping("/items/{id}/quantity")
    public Result<Void> updateQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        cartService.updateQuantity(id, quantity);
        return Result.success();
    }

    @ApiOperation("修改选中状态")
    @PutMapping("/items/{id}/checked")
    public Result<Void> updateChecked(@PathVariable Long id, @RequestParam Integer checked) {
        cartService.updateChecked(id, checked);
        return Result.success();
    }

    @ApiOperation("全选/取消全选")
    @PutMapping("/checkAll")
    public Result<Void> checkAll(@RequestParam Integer checked) {
        cartService.checkAll(checked);
        return Result.success();
    }

    @ApiOperation("删除购物车项")
    @DeleteMapping("/items/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        cartService.removeCartItem(id);
        return Result.success();
    }

    @ApiOperation("批量删除购物车项")
    @DeleteMapping("/items")
    public Result<Void> removeBatch(@RequestBody List<Long> ids) {
        cartService.removeCartItems(ids);
        return Result.success();
    }

    @ApiOperation("购物车统计")
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        List<CartItem> items = cartService.getMyCart();
        int count = items.stream().mapToInt(CartItem::getQuantity).sum();
        BigDecimal total = cartService.calcCheckedTotal();
        return Result.success(Map.of("count", count, "totalAmount", total, "itemCount", items.size()));
    }
}
