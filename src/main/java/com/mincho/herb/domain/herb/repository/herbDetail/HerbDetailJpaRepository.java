package com.mincho.herb.domain.herb.repository.herbDetail;

import com.mincho.herb.domain.herb.entity.HerbDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HerbDetailJpaRepository extends JpaRepository<HerbDetailEntity, Long> {
    HerbDetailEntity findByCntntsSj(String cntntsSj);
}
