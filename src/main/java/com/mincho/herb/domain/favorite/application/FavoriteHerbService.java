package com.mincho.herb.domain.favorite.application;


public interface FavoriteHerbService {

    void removeFavoriteHerb(Long favoriteHerbId, String email);
    void addFavoriteHerb(String url, String email, String herbName);
}
