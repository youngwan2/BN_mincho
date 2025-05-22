package com.mincho.herb.domain.herb.repository;

import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import com.mincho.herb.domain.herb.dto.HerbStatisticsDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbJpaRepository;
import com.mincho.herb.domain.herb.repository.herb.HerbRepositoryImpl;
import com.mincho.herb.global.page.PageInfoDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link HerbRepositoryImpl}의 기능을 검증하기 위한 통합 테스트 클래스입니다.
 * <p>
 * JPA 기반 저장, 조회, 필터링, 통계 관련 커스텀 쿼리 동작을 검증합니다.
 */
@DataJpaTest
class HerbRepositoryImplTest {

    @Autowired
    private HerbJpaRepository herbJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private HerbRepositoryImpl herbRepository;

    /**
     * 각 테스트 실행 전에 {@link JPAQueryFactory}를 사용하여 {@link HerbRepositoryImpl} 인스턴스를 초기화합니다.
     */
    @BeforeEach
    void setUp() {
        herbRepository = new HerbRepositoryImpl(herbJpaRepository, new JPAQueryFactory(entityManager));
    }

    /**
     * HerbEntity 저장 기능 테스트입니다.
     * <p>
     * 주어진 약초 데이터를 저장한 후, ID가 정상적으로 생성되고 제목이 일치하는지 검증합니다.
     */
    @Test
    void save_ShouldSaveHerbEntity() {
        // given
        HerbEntity herbEntity = new HerbEntity();
        herbEntity.setCntntsSj("Test Herb");
        herbEntity.setCreatedAt(LocalDateTime.now());

        // when
        HerbEntity savedEntity = herbRepository.save(herbEntity);

        // then
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getCntntsSj()).isEqualTo("Test Herb");
    }

    /**
     * ID로 HerbEntity 조회 기능 테스트입니다.
     * <p>
     * 저장된 약초를 ID로 조회했을 때, 해당 데이터가 정확히 반환되는지 검증합니다.
     */
    @Test
    void findById_ShouldReturnHerbEntity_whenExists() {
        // given
        HerbEntity herbEntity = new HerbEntity();
        herbEntity.setCntntsSj("Test Herb");
        herbEntity.setCreatedAt(LocalDateTime.now());
        herbJpaRepository.save(herbEntity);

        // when
        HerbEntity foundEntity = herbRepository.findById(herbEntity.getId());

        // then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getCntntsSj()).isEqualTo("Test Herb");
    }

    /**
     * 필터링 기능 테스트입니다.
     * <p>
     * 제목(cntntsSj)을 기준으로 필터링한 결과가 조건에 맞게 반환되는지 검증합니다.
     */
    @Test
    void findByFiltering_ShouldReturnFilteredHerbs() {
        // given
        HerbEntity herbEntity1 = new HerbEntity();
        herbEntity1.setCntntsSj("Herb A");
        herbEntity1.setCreatedAt(LocalDateTime.now());
        herbJpaRepository.save(herbEntity1);

        HerbEntity herbEntity2 = new HerbEntity();
        herbEntity2.setCntntsSj("Herb B");
        herbEntity2.setCreatedAt(LocalDateTime.now());
        herbJpaRepository.save(herbEntity2);

        HerbSort herbSort = new HerbSort(); // 정렬 조건 없음
        HerbFilteringRequestDTO requestDTO = new HerbFilteringRequestDTO();
        requestDTO.setCntntsSj("Herb A"); // "Herb A" 제목으로 필터링
        PageInfoDTO pageInfoDTO = PageInfoDTO.builder()
                .page(0)
                .size(10)
                .build();

        // when
        List<HerbDTO> filteredHerbs = herbRepository.findByFiltering(requestDTO, herbSort, pageInfoDTO);

        // then
        assertThat(filteredHerbs).hasSize(1);
        assertThat(filteredHerbs.get(0).getCntntsSj()).isEqualTo("Herb A");
    }

    /**
     * 약초 통계 조회 기능 테스트입니다.
     * <p>
     * 저장된 약초 데이터를 기준으로 통계 정보(총 개수 등)가 정상적으로 반환되는지 검증합니다.
     */
    @Test
    void findHerbStatics_ShouldReturnHerbStatistics() {
        // given
        HerbEntity herbEntity = new HerbEntity();
        herbEntity.setCntntsSj("Herb A");
        herbEntity.setCreatedAt(LocalDateTime.now());
        herbJpaRepository.save(herbEntity);

        // when
        HerbStatisticsDTO statistics = herbRepository.findHerbStatics();

        // then
        assertThat(statistics.getTotalCount()).isGreaterThan(0);
    }
}

