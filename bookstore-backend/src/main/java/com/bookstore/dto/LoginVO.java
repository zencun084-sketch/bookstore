package com.bookstore.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String role;
}
