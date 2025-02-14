package com.mincho.herb.domain.herb.repository.herbRatings;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;

import java.util.List;

public interface HerbRatingsRepository {

    void save(HerbRatingsEntity herbRatingsEntity);
    List<HerbRatingsEntity> findAllBy(HerbEntity herbEntity);
}
