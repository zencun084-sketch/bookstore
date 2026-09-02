package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.common.BusinessException;
import com.bookstore.common.ResultCode;
import com.bookstore.entity.Favorite;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.security.UserContext;
import com.bookstore.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteMapper favoriteMapper;

    public FavoriteServiceImpl(FavoriteMapper favoriteMapper) {
        this.favoriteMapper = favoriteMapper;
    }

    @Override
    public List<Favorite> myFavorites() {
        return favoriteMapper.selectFavoritesWithBook(UserContext.getCurrentUserId());
    }

    @Override
    public void addFavorite(Long bookId) {
        Long userId = UserContext.getCurrentUserId();
        // 检查是否已收藏
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, userId)
                .eq(Favorite::getBookId, bookId));
        if (count > 0) {
            throw new BusinessException(ResultCode.FAVORITE_EXISTS);
        }
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        favoriteMapper.insert(favorite);
    }

    @Override
    public void removeFavorite(Long bookId) {
        favoriteMapper.delete(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, UserContext.getCurrentUserId())
                .eq(Favorite::getBookId, bookId));
    }

    @Override
    public boolean isFavorite(Long bookId) {
        Long count = favoriteMapper.selectCount(new LambdaQueryWrapper<Favorite>()
                .eq(Favorite::getUserId, UserContext.getCurrentUserId())
                .eq(Favorite::getBookId, bookId));
        return count > 0;
    }
}
