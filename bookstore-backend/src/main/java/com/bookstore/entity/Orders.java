package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单
 */
@Data
@TableName("orders")
public class Orders {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;

    /**
     * 0-待付款 1-待发货 2-已发货 3-已完成 4-已取消
     */
    private Integer status;

    private String receiver;
    private String phone;
    private String address;
    private String remark;
    private LocalDateTime payTime;
    private LocalDateTime shipTime;
    private LocalDateTime finishTime;
    private LocalDateTime cancelTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @JsonIgnore
    private Integer isDeleted;

    /**
     * 订单明细(非数据库字段)
     */
    @TableField(exist = false)
    private List<OrderItem> items;

    /**
     * 用户昵称(非数据库字段, 用于后台展示)
     */
    @TableField(exist = false)
    private String userNickname;

    /**
     * 状态描述(非数据库字段)
     */
    @TableField(exist = false)
    private String statusText;

    public String getStatusText() {
        if (status == null) return null;
        switch (status) {
            case 0: return "待付款";
            case 1: return "待发货";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            default: return "未知";
        }
    }
}
