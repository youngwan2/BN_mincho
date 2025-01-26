package com.mincho.herb.domain.herb.repository.herbDetail;


import com.mincho.herb.domain.herb.entity.HerbDetailEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbDetailRepositoryImpl implements HerbDetailRepository{

    private final HerbDetailJpaRepository herbDetailJpaRepository;

    @Override
    public void saveAll(List<HerbDetailEntity> herbDetailEntities) {
        herbDetailJpaRepository.saveAll(herbDetailEntities);
    }
}
