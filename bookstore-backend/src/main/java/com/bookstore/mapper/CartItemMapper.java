package com.bookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookstore.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 查询购物车项(含图书信息)
     */
    List<CartItem> selectCartItemsWithBook(@Param("cartId") Long cartId);

    /**
     * 查询选中的购物车项(含图书信息)
     */
    List<CartItem> selectCheckedCartItemsWithBook(@Param("cartId") Long cartId);
}
