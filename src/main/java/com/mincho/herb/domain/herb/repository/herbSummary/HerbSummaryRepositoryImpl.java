package com.mincho.herb.domain.herb.repository.herbSummary;

import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbSummaryRepositoryImpl implements  HerbSummaryRepository{

    private final HerbSummaryJpaRepository herbSummaryJpaRepository;

    @Override
    public void saveAll(List<HerbSummaryEntity> herbSummaries) {
        herbSummaryJpaRepository.saveAll(herbSummaries);
    }

    @Override
    public Page<HerbSummaryEntity> findAllPaging(Pageable pageable) {
        return herbSummaryJpaRepository.findAll(pageable);
    }
}
