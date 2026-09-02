package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.entity.Notice;
import com.bookstore.mapper.NoticeMapper;
import com.bookstore.service.NoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    @Override
    public List<Notice> listPublished() {
        return list(new LambdaQueryWrapper<Notice>()
                .eq(Notice::getStatus, 1)
                .orderByDesc(Notice::getUpdateTime));
    }

    @Override
    public List<Notice> listAll() {
        return list(new LambdaQueryWrapper<Notice>()
                .orderByDesc(Notice::getUpdateTime));
    }
}
