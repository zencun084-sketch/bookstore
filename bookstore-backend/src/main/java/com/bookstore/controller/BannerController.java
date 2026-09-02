package com.bookstore.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Banner;
import com.bookstore.security.UserContext;
import com.bookstore.service.BannerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Banner控制器
 */
@Api(tags = "轮播图接口")
@RestController
@RequestMapping("/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @ApiOperation("启用的Banner列表(前台)")
    @GetMapping
    public Result<List<Banner>> list() {
        return Result.success(bannerService.listEnabled());
    }

    @ApiOperation("所有Banner(管理员)")
    @GetMapping("/all")
    public Result<List<Banner>> listAll() {
        checkAdmin();
        return Result.success(bannerService.listAll());
    }

    @ApiOperation("新增Banner(管理员)")
    @PostMapping
    public Result<Void> add(@RequestBody Banner banner) {
        checkAdmin();
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        bannerService.save(banner);
        return Result.success();
    }

    @ApiOperation("修改Banner(管理员)")
    @PutMapping
    public Result<Void> update(@RequestBody Banner banner) {
        checkAdmin();
        bannerService.updateById(banner);
        return Result.success();
    }

    @ApiOperation("删除Banner(管理员)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        bannerService.removeById(id);
        return Result.success();
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
