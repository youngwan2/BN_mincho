package com.mincho.herb.domain.herb.repository.herbViews;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbViewsEntity;

public interface HerbViewsRepository {

    HerbViewsEntity save(HerbViewsEntity herbViews);

    HerbViewsEntity findByHerb(HerbEntity herbEntity);

}
