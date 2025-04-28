package com.mincho.herb.domain.herb.repository.herbViews;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HerbViewsJpaRepository extends JpaRepository<HerbViewsEntity, Long> {


        @Query("SELECT hv FROM HerbViewsEntity hv WHERE hv.herb = :herbEntity")
        HerbViewsEntity findByHerb(HerbEntity herbEntity);

}
