package com.mincho.herb.domain.herb.repository.herbDetail;

import com.mincho.herb.domain.herb.domain.HerbDetail;
import com.mincho.herb.domain.herb.entity.HerbDetailEntity;

import java.util.List;

public interface HerbDetailRepository {

    void saveAll(List<HerbDetailEntity> herbDetailEntities);

    HerbDetail findByCntntsSj(String herbName);
}
