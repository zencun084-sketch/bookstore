package com.bookstore.controller;

import com.bookstore.common.Result;
import com.bookstore.entity.Favorite;
import com.bookstore.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@Api(tags = "收藏接口")
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @ApiOperation("我的收藏列表")
    @GetMapping
    public Result<List<Favorite>> list() {
        return Result.success(favoriteService.myFavorites());
    }

    @ApiOperation("添加收藏")
    @PostMapping("/{bookId}")
    public Result<Void> add(@PathVariable Long bookId) {
        favoriteService.addFavorite(bookId);
        return Result.success();
    }

    @ApiOperation("取消收藏")
    @DeleteMapping("/{bookId}")
    public Result<Void> remove(@PathVariable Long bookId) {
        favoriteService.removeFavorite(bookId);
        return Result.success();
    }

    @ApiOperation("检查是否已收藏")
    @GetMapping("/check/{bookId}")
    public Result<Map<String, Boolean>> check(@PathVariable Long bookId) {
        return Result.success(Map.of("isFavorite", favoriteService.isFavorite(bookId)));
    }
}
