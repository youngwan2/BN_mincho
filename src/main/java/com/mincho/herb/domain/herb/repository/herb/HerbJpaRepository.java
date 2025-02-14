package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HerbJpaRepository extends JpaRepository<HerbEntity, Long> {

    // 페이징 처리
    Page<HerbEntity> findAll(Pageable pageable);


    HerbEntity findByCntntsSj(String herbName);

    @Query("SELECT h FROM HerbEntity h WHERE h.id = :herbId")
    Optional<HerbEntity> findSummaryById(@Param("herbId") Long herbId);
}
