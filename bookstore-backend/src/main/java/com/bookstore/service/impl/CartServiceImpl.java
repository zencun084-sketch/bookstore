package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Book;
import com.bookstore.entity.Cart;
import com.bookstore.entity.CartItem;
import com.bookstore.mapper.CartItemMapper;
import com.bookstore.mapper.CartMapper;
import com.bookstore.security.UserContext;
import com.bookstore.service.BookService;
import com.bookstore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final BookService bookService;

    /**
     * 获取或创建用户购物车
     */
    private Cart getOrCreateCart(Long userId) {
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>().eq(Cart::getUserId, userId));
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cartMapper.insert(cart);
        }
        return cart;
    }

    @Override
    public List<CartItem> getMyCart() {
        Long userId = UserContext.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        List<CartItem> items = cartItemMapper.selectCartItemsWithBook(cart.getId());
        // 计算小计
        for (CartItem item : items) {
            if (item.getBook() != null && item.getBook().getPrice() != null) {
                item.setSubtotal(item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return items;
    }

    @Override
    public void addToCart(Long bookId, Integer quantity) {
        Long userId = UserContext.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);

        // 校验图书
        Book book = bookService.getById(bookId);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        if (book.getStatus() == 0) {
            throw new BusinessException(ResultCode.BOOK_OFF_SHELF);
        }

        // 查询是否已存在
        CartItem existItem = cartItemMapper.selectOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .eq(CartItem::getBookId, bookId));

        if (existItem != null) {
            // 校验库存
            int newQuantity = existItem.getQuantity() + quantity;
            if (newQuantity > book.getStock()) {
                throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
            }
            existItem.setQuantity(newQuantity);
            cartItemMapper.updateById(existItem);
        } else {
            if (quantity > book.getStock()) {
                throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
            }
            CartItem item = new CartItem();
            item.setCartId(cart.getId());
            item.setBookId(bookId);
            item.setQuantity(quantity);
            item.setChecked(1);
            cartItemMapper.insert(item);
        }
    }

    @Override
    public void updateQuantity(Long cartItemId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "数量必须大于0");
        }
        CartItem item = cartItemMapper.selectById(cartItemId);
        if (item == null) {
            throw new BusinessException(ResultCode.CART_ITEM_NOT_FOUND);
        }
        // 校验库存
        Book book = bookService.getById(item.getBookId());
        if (book != null && quantity > book.getStock()) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }
        item.setQuantity(quantity);
        cartItemMapper.updateById(item);
    }

    @Override
    public void updateChecked(Long cartItemId, Integer checked) {
        CartItem item = new CartItem();
        item.setId(cartItemId);
        item.setChecked(checked);
        cartItemMapper.updateById(item);
    }

    @Override
    public void checkAll(Integer checked) {
        Long userId = UserContext.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        cartItemMapper.update(null, new LambdaUpdateWrapper<CartItem>()
                .eq(CartItem::getCartId, cart.getId())
                .set(CartItem::getChecked, checked));
    }

    @Override
    public void removeCartItem(Long cartItemId) {
        cartItemMapper.deleteById(cartItemId);
    }

    @Override
    public void removeCartItems(List<Long> ids) {
        cartItemMapper.deleteBatchIds(ids);
    }

    @Override
    public List<CartItem> getCheckedItems() {
        Long userId = UserContext.getCurrentUserId();
        Cart cart = getOrCreateCart(userId);
        return cartItemMapper.selectCheckedCartItemsWithBook(cart.getId());
    }

    @Override
    public BigDecimal calcCheckedTotal() {
        List<CartItem> items = getCheckedItems();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : items) {
            if (item.getBook() != null && item.getBook().getPrice() != null) {
                total = total.add(item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())));
            }
        }
        return total;
    }
}
