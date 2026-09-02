package com.bookstore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Book;
import com.bookstore.security.UserContext;
import com.bookstore.service.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 图书控制器
 */
@Api(tags = "图书接口")
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // ============ 前台接口(无需登录) ============

    @ApiOperation("图书列表(分页/搜索/筛选/排序)")
    @GetMapping("/list")
    public Result<IPage<Book>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortOrder) {
        return Result.success(bookService.bookPage(current, size, keyword, categoryId,
                minPrice, maxPrice, sortField, sortOrder));
    }

    @ApiOperation("图书搜索")
    @GetMapping("/search")
    public Result<IPage<Book>> search(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "12") Integer size,
            @RequestParam String keyword) {
        return Result.success(bookService.bookPage(current, size, keyword, null, null, null, null, null));
    }

    @ApiOperation("图书详情")
    @GetMapping("/detail/{id}")
    public Result<Book> detail(@PathVariable Long id) {
        Book book = bookService.getById(id);
        if (book == null) {
            throw new BusinessException(ResultCode.BOOK_NOT_FOUND);
        }
        return Result.success(book);
    }

    @ApiOperation("热门图书")
    @GetMapping("/hot")
    public Result<List<Book>> hot(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.hotBooks(limit));
    }

    @ApiOperation("新书推荐")
    @GetMapping("/new")
    public Result<List<Book>> newBooks(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(bookService.newBooks(limit));
    }

    // ============ 后台管理接口(需管理员权限) ============

    @ApiOperation("分页查询图书(管理员)")
    @GetMapping("/page")
    public Result<IPage<Book>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        checkAdmin();
        return Result.success(bookService.bookPage(current, size, keyword, null, null, null, null, null));
    }

    @ApiOperation("新增图书(管理员)")
    @PostMapping
    public Result<Void> add(@RequestBody Book book) {
        checkAdmin();
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        if (book.getSales() == null) {
            book.setSales(0);
        }
        bookService.save(book);
        return Result.success();
    }

    @ApiOperation("修改图书(管理员)")
    @PutMapping
    public Result<Void> update(@RequestBody Book book) {
        checkAdmin();
        bookService.updateById(book);
        return Result.success();
    }

    @ApiOperation("删除图书(管理员)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        bookService.removeById(id);
        return Result.success();
    }

    @ApiOperation("图书上下架(管理员)")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        checkAdmin();
        bookService.updateStatus(id, status);
        return Result.success();
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
