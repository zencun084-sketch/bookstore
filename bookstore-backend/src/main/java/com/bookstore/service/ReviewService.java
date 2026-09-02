package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Review;

public interface ReviewService extends IService<Review> {

    /**
     * 图书评论分页
     */
    IPage<Review> bookReviews(Long bookId, Integer current, Integer size);

    /**
     * 发表评论
     */
    void addReview(Review review);

    /**
     * 后台评论分页
     */
    IPage<Review> adminReviews(Integer current, Integer size);

    /**
     * 点赞
     */
    void like(Long reviewId);

    /**
     * 审核评论(显示/隐藏)
     */
    void updateStatus(Long id, Integer status);

    /**
     * 图书平均评分
     */
    Double avgRating(Long bookId);
}
