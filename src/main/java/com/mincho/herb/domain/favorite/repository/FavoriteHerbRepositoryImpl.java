package com.mincho.herb.domain.favorite.repository;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.favorite.entity.FavoriteHerbEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FavoriteHerbRepositoryImpl implements FavoriteHerbRepository {

    private final FavoriteHerbJpaRepository favoriteHerbJpaRepository;

    @Override
    public void save(FavoriteHerbEntity favoriteHerbEntity) {
        favoriteHerbJpaRepository.save(favoriteHerbEntity);
    }

    @Override
    public void deleteMemberIdAndFavoriteHerbId(Long memberId, Long favoriteHerbId) {
        favoriteHerbJpaRepository.deleteByMemberIdAndId(memberId, favoriteHerbId).orElseThrow(() -> new CustomHttpException(HttpErrorCode.CONFLICT,"관심허브 삭제 요청이 실패하였습니다."));
    }
}
