package com.mincho.herb.domain.herb.repository.herbViews;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbViewsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class HerbViewsRepositoryImpl implements HerbViewsRepository{

    private final HerbViewsJpaRepository herbViewsJpaRepository;
    
    // 조회수 저장
    @Override
    public HerbViewsEntity save(HerbViewsEntity herbViews) {
        return herbViewsJpaRepository.save(herbViews);
    }

    // 조회수 조회
    @Override
    public HerbViewsEntity findByHerb(HerbEntity herbEntity) {
        return herbViewsJpaRepository.findByHerb(herbEntity);
    }
}
