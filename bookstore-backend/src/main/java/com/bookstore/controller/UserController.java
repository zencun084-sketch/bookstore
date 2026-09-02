package com.bookstore.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.dto.ChangePasswordDTO;
import com.bookstore.entity.User;
import com.bookstore.security.UserContext;
import com.bookstore.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户控制器
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public Result<User> info() {
        return Result.success(userService.getCurrentUserInfo());
    }

    @ApiOperation("更新个人资料")
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody User user) {
        userService.updateProfile(user);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody ChangePasswordDTO dto) {
        userService.changePassword(dto);
        return Result.success();
    }

    @ApiOperation("更新头像")
    @PutMapping("/avatar")
    public Result<Void> updateAvatar(@RequestParam String avatar) {
        userService.updateAvatar(avatar);
        return Result.success();
    }

    // ============ 后台管理接口 ============

    @ApiOperation("分页查询用户(管理员)")
    @GetMapping("/page")
    public Result<Page<User>> page(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        Page<User> page = new Page<>(current, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreateTime);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getNickname, keyword);
        }
        return Result.success(userService.page(page, wrapper));
    }

    @ApiOperation("冻结/恢复账号(管理员)")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        User user = new User();
        user.setId(id);
        user.setStatus(status);
        userService.updateById(user);
        return Result.success();
    }

    @ApiOperation("根据ID查询用户(管理员)")
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        return Result.success(userService.getById(id));
    }
}
