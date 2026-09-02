package com.bookstore.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 系统状态码
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "成功"),
    ERROR(500, "系统错误"),

    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),

    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    EMAIL_EXISTS(1003, "邮箱已存在"),
    PASSWORD_ERROR(1004, "密码错误"),
    ACCOUNT_FROZEN(1005, "账号已被冻结"),
    OLD_PASSWORD_ERROR(1006, "原密码错误"),

    // 图书相关 2xxx
    BOOK_NOT_FOUND(2001, "图书不存在"),
    BOOK_OFF_SHELF(2002, "图书已下架"),
    STOCK_NOT_ENOUGH(2003, "库存不足"),

    // 购物车相关 3xxx
    CART_ITEM_NOT_FOUND(3001, "购物车项不存在"),

    // 订单相关 4xxx
    ORDER_NOT_FOUND(4001, "订单不存在"),
    ORDER_STATUS_ERROR(4002, "订单状态不允许此操作"),
    CART_EMPTY(4003, "购物车为空"),

    // 收藏相关 5xxx
    FAVORITE_EXISTS(5001, "已收藏过该图书");

    private final Integer code;
    private final String message;
}
