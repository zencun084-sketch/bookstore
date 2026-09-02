package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Book;
import com.bookstore.mapper.BookMapper;
import com.bookstore.service.BookService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    @Override
    public IPage<Book> bookPage(Integer current, Integer size, String keyword, Long categoryId,
                               BigDecimal minPrice, BigDecimal maxPrice, String sortField, String sortOrder) {
        Page<Book> page = new Page<>(current, size);
        return baseMapper.selectBookPage(page, keyword, categoryId, minPrice, maxPrice, sortField, sortOrder);
    }

    @Override
    public List<Book> hotBooks(int limit) {
        return baseMapper.selectHotBooks(limit);
    }

    @Override
    public List<Book> newBooks(int limit) {
        return baseMapper.selectNewBooks(limit);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        Book update = new Book();
        update.setId(id);
        update.setStatus(status);
        updateById(update);
    }

    @Override
    public void addSales(Long bookId, int quantity) {
        int rows = baseMapper.addSales(bookId, quantity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
    }

    @Override
    public void reduceStock(Long bookId, int quantity) {
        int rows = baseMapper.reduceStock(bookId, quantity);
        if (rows == 0) {
            throw new BusinessException(ResultCode.STOCK_NOT_ENOUGH);
        }
    }
}
