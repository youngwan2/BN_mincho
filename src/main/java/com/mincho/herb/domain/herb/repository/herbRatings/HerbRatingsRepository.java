package com.mincho.herb.domain.herb.repository.herbRatings;

import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;

import java.util.List;

public interface HerbRatingsRepository {

    void save(HerbRatingsEntity herbRatingsEntity);
    List<HerbRatingsEntity> findAllBy(HerbSummaryEntity herbSummaryEntity);
}
