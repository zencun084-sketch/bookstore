package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.dto.ChangePasswordDTO;
import com.bookstore.dto.LoginDTO;
import com.bookstore.dto.LoginVO;
import com.bookstore.dto.RegisterDTO;
import com.bookstore.entity.User;
import com.bookstore.mapper.UserMapper;
import com.bookstore.security.JwtUtils;
import com.bookstore.security.UserContext;
import com.bookstore.service.UserService;
import com.bookstore.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtils jwtUtils;

    @Override
    public void register(RegisterDTO dto) {
        // 校验用户名唯一
        long usernameCount = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername()));
        if (usernameCount > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }

        // 校验邮箱唯一
        long emailCount = count(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, dto.getEmail()));
        if (emailCount > 0) {
            throw new BusinessException(ResultCode.EMAIL_EXISTS);
        }

        // 创建用户
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(PasswordUtils.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getUsername());
        user.setRole("USER");
        user.setStatus(1);
        user.setGender(0);
        save(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        // 按用户名或邮箱查询
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, dto.getUsername())
                .or()
                .eq(User::getEmail, dto.getUsername()));

        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验密码
        if (!PasswordUtils.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 校验状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_FROZEN);
        }

        // 生成Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        return vo;
    }

    @Override
    public User getCurrentUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public void updateProfile(User user) {
        Long userId = UserContext.getCurrentUserId();
        user.setId(userId);
        // 不允许通过此接口修改密码、角色、状态
        user.setPassword(null);
        user.setRole(null);
        user.setStatus(null);
        user.setUsername(null);
        updateById(user);
    }

    @Override
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = UserContext.getCurrentUserId();
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验原密码
        if (!PasswordUtils.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.OLD_PASSWORD_ERROR);
        }

        // 更新密码
        User update = new User();
        update.setId(userId);
        update.setPassword(PasswordUtils.encode(dto.getNewPassword()));
        updateById(update);
    }

    @Override
    public void updateAvatar(String avatar) {
        Long userId = UserContext.getCurrentUserId();
        User update = new User();
        update.setId(userId);
        update.setAvatar(avatar);
        updateById(update);
    }
}
