package com.mincho.herb.domain.herb.repository.herbSummary;

import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface HerbSummaryJpaRepository extends JpaRepository<HerbSummaryEntity, Long> {

    Page<HerbSummaryEntity> findAll(Pageable pageable);
    HerbSummaryEntity findByCntntsSj(String herbName);
}
