package com.mincho.herb.domain.herb.repository.herbRatings;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbRatingsRepositoryImpl implements HerbRatingsRepository {

    private final HerbRatingsJpaRepository herbRatingsJpaRepository;

    @Override
    public void save(HerbRatingsEntity herbRatingsEntity) {
        herbRatingsJpaRepository.save(herbRatingsEntity);
    }


    @Override
    public List<HerbRatingsEntity> findAllBy(HerbEntity herbEntity) {
        return herbRatingsJpaRepository.findAllByHerbId(herbEntity.getId());
    }
}
