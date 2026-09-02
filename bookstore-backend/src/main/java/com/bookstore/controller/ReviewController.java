package com.bookstore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.common.Result;
import com.bookstore.entity.Review;
import com.bookstore.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论控制器
 */
@Api(tags = "评论接口")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @ApiOperation("图书评论列表(分页)")
    @GetMapping("/book/{bookId}")
    public Result<IPage<Review>> bookReviews(
            @PathVariable Long bookId,
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.bookReviews(bookId, current, size));
    }

    @ApiOperation("图书平均评分")
    @GetMapping("/book/{bookId}/rating")
    public Result<Map<String, Object>> avgRating(@PathVariable Long bookId) {
        Double rating = reviewService.avgRating(bookId);
        return Result.success(Map.of("avgRating", rating != null ? rating : 0.0));
    }

    @ApiOperation("发表评论")
    @PostMapping
    public Result<Void> add(@RequestBody Review review) {
        reviewService.addReview(review);
        return Result.success();
    }

    @ApiOperation("点赞评论")
    @PutMapping("/{id}/like")
    public Result<Void> like(@PathVariable Long id) {
        reviewService.like(id);
        return Result.success();
    }

    // ============ 后台 ============

    @ApiOperation("评论分页(管理员)")
    @GetMapping("/page")
    public Result<IPage<Review>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.adminReviews(current, size));
    }

    @ApiOperation("审核评论(管理员)")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        reviewService.updateStatus(id, status);
        return Result.success();
    }

    @ApiOperation("删除评论(管理员)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        reviewService.removeById(id);
        return Result.success();
    }
}
