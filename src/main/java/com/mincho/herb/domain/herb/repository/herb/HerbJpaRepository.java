package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface HerbJpaRepository extends JpaRepository<HerbEntity, Long> {

    // 페이징 처리된 약초목록 조회
    Page<HerbEntity> findAll(Pageable pageable);
    
    HerbEntity findByCntntsSj(String herbName); // 약초명으로 찾기
}
