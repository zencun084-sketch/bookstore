package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Book;

import java.math.BigDecimal;
import java.util.List;

public interface BookService extends IService<Book> {

    /**
     * 分页查询图书
     */
    IPage<Book> bookPage(Integer current, Integer size, String keyword, Long categoryId,
                         BigDecimal minPrice, BigDecimal maxPrice, String sortField, String sortOrder);

    /**
     * 热门图书
     */
    List<Book> hotBooks(int limit);

    /**
     * 新书推荐
     */
    List<Book> newBooks(int limit);

    /**
     * 图书上下架
     */
    void updateStatus(Long id, Integer status);

    /**
     * 增加销量
     */
    void addSales(Long bookId, int quantity);

    /**
     * 减少库存
     */
    void reduceStock(Long bookId, int quantity);
}
