package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Review;
import com.bookstore.mapper.ReviewMapper;
import com.bookstore.security.UserContext;
import com.bookstore.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl extends ServiceImpl<ReviewMapper, Review> implements ReviewService {

    @Override
    public IPage<Review> bookReviews(Long bookId, Integer current, Integer size) {
        Page<Review> page = new Page<>(current, size);
        return baseMapper.selectReviewPage(page, bookId);
    }

    @Override
    public void addReview(Review review) {
        review.setUserId(UserContext.getCurrentUserId());
        if (review.getRating() == null) {
            review.setRating(5);
        }
        if (review.getLikes() == null) {
            review.setLikes(0);
        }
        review.setStatus(1);
        save(review);
    }

    @Override
    public IPage<Review> adminReviews(Integer current, Integer size) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Page<Review> page = new Page<>(current, size);
        return baseMapper.selectReviewPage(page, null);
    }

    @Override
    public void like(Long reviewId) {
        Review review = getById(reviewId);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评论不存在");
        }
        Review update = new Review();
        update.setId(reviewId);
        update.setLikes(review.getLikes() + 1);
        updateById(update);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Review update = new Review();
        update.setId(id);
        update.setStatus(status);
        updateById(update);
    }

    @Override
    public Double avgRating(Long bookId) {
        return baseMapper.selectAvgRating(bookId);
    }
}
