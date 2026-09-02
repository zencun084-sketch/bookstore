package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统公告
 */
@Data
@TableName("notice")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String content;

    /**
     * 0-未发布 1-已发布 2-已下线
     */
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @JsonIgnore
    private Integer isDeleted;

    @TableField(exist = false)
    private String statusText;

    public String getStatusText() {
        if (status == null) return null;
        switch (status) {
            case 0: return "未发布";
            case 1: return "已发布";
            case 2: return "已下线";
            default: return "未知";
        }
    }
}
