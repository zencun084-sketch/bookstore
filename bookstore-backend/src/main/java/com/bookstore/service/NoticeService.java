package com.bookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Notice;

import java.util.List;

public interface NoticeService extends IService<Notice> {

    List<Notice> listPublished();

    List<Notice> listAll();
}
