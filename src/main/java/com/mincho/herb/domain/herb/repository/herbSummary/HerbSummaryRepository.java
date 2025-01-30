package com.mincho.herb.domain.herb.repository.herbSummary;

import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface HerbSummaryRepository {

    void saveAll(List<HerbSummaryEntity> herbSummaries);
    Page<HerbSummaryEntity> findAllPaging(Pageable pageable);
    HerbSummaryEntity findByCntntsSj(String herbName);
}
