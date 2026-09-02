package com.bookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Banner;

import java.util.List;

public interface BannerService extends IService<Banner> {

    List<Banner> listEnabled();

    List<Banner> listAll();
}
