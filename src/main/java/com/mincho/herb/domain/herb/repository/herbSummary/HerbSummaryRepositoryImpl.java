package com.mincho.herb.domain.herb.repository.herbSummary;

import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbSummaryRepositoryImpl implements  HerbSummaryRepository{

    private final HerbSummaryJpaRepository herbSummaryJpaRepository;

    @Override
    public void saveAll(List<HerbSummaryEntity> herbSummaries) {
        herbSummaryJpaRepository.saveAll(herbSummaries);
    }
}
