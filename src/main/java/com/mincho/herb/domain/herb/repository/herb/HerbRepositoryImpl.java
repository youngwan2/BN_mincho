package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbRepositoryImpl implements HerbRepository {

    private final HerbJpaRepository herbJpaRepository;

    @Override
    public void saveAll(List<HerbEntity> herbs) {
        herbJpaRepository.saveAll(herbs);
    }

    @Override
    public Page<HerbEntity> findAllPaging(Pageable pageable) {
        return herbJpaRepository.findAll(pageable);
    }

    @Override
    public HerbEntity findByCntntsSj(String herbName) {
        return herbJpaRepository.findByCntntsSj(herbName);
    }

    @Override
    public HerbEntity findDetailsById() {

        return null;
    }
}
