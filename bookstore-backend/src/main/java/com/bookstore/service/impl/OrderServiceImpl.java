package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.dto.CreateOrderDTO;
import com.bookstore.entity.*;
import com.bookstore.mapper.OrderItemMapper;
import com.bookstore.mapper.OrdersMapper;
import com.bookstore.security.UserContext;
import com.bookstore.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrderService {

    private final CartService cartService;
    private final AddressService addressService;
    private final BookService bookService;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public Orders createOrder(CreateOrderDTO dto) {
        Long userId = UserContext.getCurrentUserId();

        // 获取收货地址
        Address address = addressService.getById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "收货地址不存在");
        }

        // 获取购物车选中项
        List<CartItem> checkedItems = cartService.getCheckedItems();
        if (checkedItems.isEmpty()) {
            throw new BusinessException(ResultCode.CART_EMPTY);
        }

        // 校验库存并计算总额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : checkedItems) {
            if (item.getBook() == null) {
                throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
            }
            if (item.getBook().getStatus() == 0) {
                throw new BusinessException(ResultCode.BOOK_OFF_SHELF, item.getBook().getTitle() + " 已下架");
            }
            if (item.getQuantity() > item.getBook().getStock()) {
                throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH, item.getBook().getTitle() + " 库存不足");
            }
            totalAmount = totalAmount.add(item.getBook().getPrice().multiply(new BigDecimal(item.getQuantity())));
        }

        // 创建订单
        Orders order = new Orders();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0); // 待付款
        order.setReceiver(address.getReceiver());
        order.setPhone(address.getPhone());
        order.setAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetail());
        order.setRemark(dto.getRemark());
        save(order);

        // 创建订单明细 + 扣减库存 + 增加销量
        for (CartItem item : checkedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setBookId(item.getBookId());
            orderItem.setBookTitle(item.getBook().getTitle());
            orderItem.setBookCover(item.getBook().getCover());
            orderItem.setPrice(item.getBook().getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItemMapper.insert(orderItem);

            // 扣减库存、增加销量
            bookService.reduceStock(item.getBookId(), item.getQuantity());
            bookService.addSales(item.getBookId(), item.getQuantity());
        }

        // 清空购物车选中项
        cartService.removeCartItems(checkedItems.stream().map(CartItem::getId).collect(java.util.stream.Collectors.toList()));

        // 加载明细返回
        order.setItems(orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId())));
        return order;
    }

    @Override
    public void payOrder(Long orderId) {
        Orders order = checkOrderOwner(orderId);
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR);
        }
        Orders update = new Orders();
        update.setId(orderId);
        update.setStatus(1); // 待发货
        update.setPayTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Orders order = checkOrderOwner(orderId);
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不可取消");
        }

        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Book book = bookService.getById(item.getBookId());
            if (book != null) {
                book.setStock(book.getStock() + item.getQuantity());
                book.setSales(Math.max(0, book.getSales() - item.getQuantity()));
                bookService.updateById(book);
            }
        }

        Orders update = new Orders();
        update.setId(orderId);
        update.setStatus(4); // 已取消
        update.setCancelTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public void confirmOrder(Long orderId) {
        Orders order = checkOrderOwner(orderId);
        if (order.getStatus() != 2) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不可确认收货");
        }
        Orders update = new Orders();
        update.setId(orderId);
        update.setStatus(3); // 已完成
        update.setFinishTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public void shipOrder(Long orderId) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Orders order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (order.getStatus() != 1) {
            throw new BusinessException(ResultCode.ORDER_STATUS_ERROR, "当前状态不可发货");
        }
        Orders update = new Orders();
        update.setId(orderId);
        update.setStatus(2); // 已发货
        update.setShipTime(LocalDateTime.now());
        updateById(update);
    }

    @Override
    public IPage<Orders> myOrders(Integer current, Integer size, Integer status) {
        Long userId = UserContext.getCurrentUserId();
        Page<Orders> page = new Page<>(current, size);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<Orders>()
                .eq(Orders::getUserId, userId)
                .orderByDesc(Orders::getCreateTime);
        if (status != null) {
            wrapper.eq(Orders::getStatus, status);
        }
        IPage<Orders> result = page(page, wrapper);
        // 加载明细
        result.getRecords().forEach(this::loadOrderItems);
        return result;
    }

    @Override
    public IPage<Orders> adminOrders(Integer current, Integer size, Integer status, String orderNo) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Page<Orders> page = new Page<>(current, size);
        IPage<Orders> result = baseMapper.selectOrderPage(page, status, orderNo);
        result.getRecords().forEach(this::loadOrderItems);
        return result;
    }

    @Override
    public Orders orderDetail(Long orderId) {
        Orders order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        // 非管理员只能查自己的订单
        if (!UserContext.isAdmin() && !order.getUserId().equals(UserContext.getCurrentUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        loadOrderItems(order);
        return order;
    }

    @Override
    public void repurchase(Long orderId) {
        Orders order = checkOrderOwner(orderId);
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            cartService.addToCart(item.getBookId(), item.getQuantity());
        }
    }

    // ============ 私有方法 ============

    private Orders checkOrderOwner(Long orderId) {
        Orders order = getById(orderId);
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(UserContext.getCurrentUserId())) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return order;
    }

    private void loadOrderItems(Orders order) {
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getOrderId, order.getId()));
        order.setItems(items);
    }

    /**
     * 生成订单号: yyyyMMddHHmmss + 6位随机数
     */
    private String generateOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }
}
