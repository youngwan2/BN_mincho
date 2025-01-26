package com.mincho.herb.domain.herb.repository.herbSummary;

import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;

import java.util.List;

public interface HerbSummaryRepository {

    void saveAll(List<HerbSummaryEntity> herbSummaries);
}
