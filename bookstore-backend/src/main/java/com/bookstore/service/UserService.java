package com.bookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.dto.ChangePasswordDTO;
import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.LoginVO;
import com.bookstore.dto.RegisterDTO;
import com.bookstore.entity.User;

public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(RegisterDTO dto);

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO dto);

    /**
     * 获取当前用户信息
     */
    User getCurrentUserInfo();

    /**
     * 更新个人资料
     */
    void updateProfile(User user);

    /**
     * 修改密码
     */
    void changePassword(ChangePasswordDTO dto);

    /**
     * 更新头像
     */
    void updateAvatar(String avatar);
}
