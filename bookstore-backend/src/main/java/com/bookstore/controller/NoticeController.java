package com.bookstore.controller;

import com.bookstore.common.BusinessException;
import com.bookstore.common.Result;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Notice;
import com.bookstore.security.UserContext;
import com.bookstore.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@Api(tags = "公告接口")
@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @ApiOperation("已发布公告(前台)")
    @GetMapping("/published")
    public Result<List<Notice>> listPublished() {
        return Result.success(noticeService.listPublished());
    }

    @ApiOperation("所有公告(管理员)")
    @GetMapping("/all")
    public Result<List<Notice>> listAll() {
        checkAdmin();
        return Result.success(noticeService.listAll());
    }

    @ApiOperation("新增公告(管理员)")
    @PostMapping
    public Result<Void> add(@RequestBody Notice notice) {
        checkAdmin();
        if (notice.getStatus() == null) {
            notice.setStatus(0);
        }
        noticeService.save(notice);
        return Result.success();
    }

    @ApiOperation("修改公告(管理员)")
    @PutMapping
    public Result<Void> update(@RequestBody Notice notice) {
        checkAdmin();
        noticeService.updateById(notice);
        return Result.success();
    }

    @ApiOperation("删除公告(管理员)")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        checkAdmin();
        noticeService.removeById(id);
        return Result.success();
    }

    private void checkAdmin() {
        if (!UserContext.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
