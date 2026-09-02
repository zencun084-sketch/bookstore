package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书实体
 */
@Data
@TableName("book")
public class Book {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Long categoryId;
    private String cover;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private Integer sales;
    private String description;

    /**
     * 0-下架 1-上架
     */
    private Integer status;

    private LocalDate publishDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    @JsonIgnore
    private Integer isDeleted;

    /**
     * 计算折扣 (用于前端展示)
     */
    @TableField(exist = false)
    private Double discount;

    public Double getDiscount() {
        if (originalPrice != null && price != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            return price.divide(originalPrice, 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(10)).doubleValue();
        }
        return null;
    }
}
