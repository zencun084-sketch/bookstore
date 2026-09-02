package com.bookstore.service;

import com.bookstore.entity.CartItem;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {

    /**
     * 获取当前用户购物车
     */
    List<CartItem> getMyCart();

    /**
     * 加入购物车
     */
    void addToCart(Long bookId, Integer quantity);

    /**
     * 修改数量
     */
    void updateQuantity(Long cartItemId, Integer quantity);

    /**
     * 修改选中状态
     */
    void updateChecked(Long cartItemId, Integer checked);

    /**
     * 全选/取消全选
     */
    void checkAll(Integer checked);

    /**
     * 删除购物车项
     */
    void removeCartItem(Long cartItemId);

    /**
     * 批量删除购物车项
     */
    void removeCartItems(List<Long> ids);

    /**
     * 获取选中项(用于下单)
     */
    List<CartItem> getCheckedItems();

    /**
     * 计算选中项总金额
     */
    BigDecimal calcCheckedTotal();
}
