package com.bookstore.service;

import com.bookstore.entity.Favorite;

import java.util.List;

public interface FavoriteService {

    List<Favorite> myFavorites();

    void addFavorite(Long bookId);

    void removeFavorite(Long bookId);

    boolean isFavorite(Long bookId);
}
