package com.mincho.herb.domain.favorite.repository;

import com.mincho.herb.domain.favorite.entity.FavoriteHerbEntity;

public interface FavoriteHerbRepository {

    void save(FavoriteHerbEntity favoriteHerbEntity);
    void deleteMemberIdAndFavoriteHerbId(Long memberId, Long favoriteHerbId);
}
