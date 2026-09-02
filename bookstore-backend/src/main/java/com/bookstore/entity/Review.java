package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图书评论
 */
@Data
@TableName("review")
public class Review {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long bookId;
    private Long userId;
    private Long orderId;
    private Integer rating;
    private String content;
    private Integer likes;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @JsonIgnore
    private Integer isDeleted;

    /**
     * 用户昵称(非数据库字段)
     */
    @TableField(exist = false)
    private String userNickname;

    /**
     * 用户头像(非数据库字段)
     */
    @TableField(exist = false)
    private String userAvatar;

    /**
     * 图书名称(非数据库字段)
     */
    @TableField(exist = false)
    private String bookTitle;
}
