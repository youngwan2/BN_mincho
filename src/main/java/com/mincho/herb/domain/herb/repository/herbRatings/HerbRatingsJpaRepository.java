package com.mincho.herb.domain.herb.repository.herbRatings;

import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HerbRatingsJpaRepository extends JpaRepository<HerbRatingsEntity, Long> {

    List<HerbRatingsEntity> findAllByUserId(Long userId);
    List<HerbRatingsEntity> findAllByHerbId(Long herbId);
}
