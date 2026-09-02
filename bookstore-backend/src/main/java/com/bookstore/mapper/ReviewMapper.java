package com.bookstore.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.entity.Review;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {

    /**
     * 分页查询图书评论(含用户信息)
     */
    IPage<Review> selectReviewPage(Page<Review> page, @Param("bookId") Long bookId);

    /**
     * 计算图书平均评分
     */
    Double selectAvgRating(@Param("bookId") Long bookId);
}
