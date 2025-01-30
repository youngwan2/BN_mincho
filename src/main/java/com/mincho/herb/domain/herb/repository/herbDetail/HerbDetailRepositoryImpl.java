package com.mincho.herb.domain.herb.repository.herbDetail;


import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.domain.HerbDetail;
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

    @Override
    public HerbDetail findByCntntsSj(String herbName) {
        HerbDetailEntity herbDetailEntity = herbDetailJpaRepository.findByCntntsSj(herbName);
        if(herbDetailEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, herbName+"으로 검색된 약초를 찾을 수 없습니다.");
        }
        return herbDetailEntity.toModel();
    }
}
