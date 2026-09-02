package com.bookstore.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建订单请求
 */
@Data
public class CreateOrderDTO {

    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    private String remark;
}
