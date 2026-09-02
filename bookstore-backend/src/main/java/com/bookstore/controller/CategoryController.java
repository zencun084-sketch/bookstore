package com.bookstore.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Category;
import com.bookstore.security.UserContext;
import com.bookstore.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@Api(tags = "分类接口")
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @ApiOperation("查询所有分类(前台可用)")
    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryService.listEnabled());
    }

    @ApiOperation("查询所有分类(含禁用, 管理员)")
    @GetMapping("/all")
    public Result<List<Category>> listAll() {
        checkAdmin();
        return Result.success(categoryService.listAll());
    }

    @ApiOperation("新增分类(管理员)")
    @PostMapping
    public Result<Void> add(@RequestBody Category category) {
        checkAdmin();
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        if (category.getSort() == null) {
            category.setSort(0);
        }
        categoryService.save(category);
        return Result.success();
    }

    @ApiOperation("修改分类(管理员)")
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        checkAdmin();
        categoryService.updateById(category);
        return Result.success();
    }

    @ApiOperation("删除分类(管理员)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        categoryService.removeById(id);
        return Result.success();
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
