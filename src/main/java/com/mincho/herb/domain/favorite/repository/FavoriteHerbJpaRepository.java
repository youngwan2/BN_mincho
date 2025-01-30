package com.mincho.herb.domain.favorite.repository;

import com.mincho.herb.domain.favorite.entity.FavoriteHerbEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteHerbJpaRepository extends JpaRepository<FavoriteHerbEntity, Long> {
   Optional<Integer> deleteByMemberIdAndHerbSummaryId(Long memberId, Long herbSummaryId);
}
