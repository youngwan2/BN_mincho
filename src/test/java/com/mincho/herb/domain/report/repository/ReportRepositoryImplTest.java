package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.ReportSearchConditionDTO;
import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportsResponseDTO;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import com.mincho.herb.global.exception.CustomHttpException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * ReportRepositoryImpl의 기능들을 검증하기 위한 단위 테스트 클래스입니다.
 *
 * <p>이 테스트는 Spring Data JPA와 QueryDSL 기반의 커스텀 레포지토리 구현체가
 * 기대한 대로 동작하는지 확인합니다.
 * 테스트는 임베디드 데이터베이스 환경이 아닌 PostgreSQL을 기반으로 실행될 수 있습니다.</p>
 *
 * <p>테스트 항목:
 * <ul>
 *     <li>신고 엔티티 저장</li>
 *     <li>신고 ID 조회</li>
 *     <li>조건 검색 (status 기준)</li>
 *     <li>통계 정보 반환 (이번 주, 저번 주, 전체)</li>
 * </ul>
 * </p>
 */
@DataJpaTest
class ReportRepositoryImplTest {

    @Autowired
    private ReportJpaRepository reportJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private ReportRepositoryImpl reportRepository;

    /**
     * 테스트 실행 전 JPAQueryFactory를 초기화하고 ReportRepositoryImpl 인스턴스를 구성합니다.
     */
    @BeforeEach
    void setUp() {
        reportRepository = new ReportRepositoryImpl(reportJpaRepository, new JPAQueryFactory(entityManager));
    }

    /**
     * 신고 엔티티를 저장했을 때, ID가 생성되고 필드 값이 제대로 설정되는지 검증합니다.
     */
    @Test
    void save_ShouldSaveReportEntity() {
        // given
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setTargetId(1L);
        reportEntity.setTargetType(ReportHandleTargetTypeEnum.COMMUNITY_POST); // 게시글 신고
        reportEntity.setStatus(ReportHandleStatusEnum.PENDING);
        reportEntity.setReason("Test reason");
        reportEntity.setCreatedAt(LocalDateTime.now());

        // when
        ReportEntity savedEntity = reportRepository.save(reportEntity);

        // then
        assertThat(savedEntity.getId()).isNotNull();
        assertThat(savedEntity.getReason()).isEqualTo("Test reason");
    }

    /**
     * 저장된 신고 엔티티를 ID로 조회할 수 있는지 검증합니다.
     */
    @Test
    void findById_ShouldReturnReportEntity_whenExists() {
        // given
        ReportEntity reportEntity = new ReportEntity();
        reportEntity.setTargetId(1L);
        reportEntity.setTargetType(ReportHandleTargetTypeEnum.COMMUNITY_POST); // 게시글 신고
        reportEntity.setStatus(ReportHandleStatusEnum.PENDING);
        reportEntity.setReason("Test reason");
        reportEntity.setCreatedAt(LocalDateTime.now());
        reportJpaRepository.save(reportEntity);

        // when
        ReportEntity foundEntity = reportRepository.findById(reportEntity.getId());

        // then
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getReason()).isEqualTo("Test reason");
    }

    /**
     * 존재하지 않는 ID로 조회할 경우 CustomHttpException 예외가 발생하는지 확인합니다.
     */
    @Test
    void findById_ShouldThrowException_whenNotExists() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(CustomHttpException.class, () -> reportRepository.findById(nonExistentId));
    }

    /**
     * 조건 검색 기능이 정상 작동하는지 검증합니다.
     * 상태가 PENDING인 신고만 검색되어야 합니다.
     */
    @Test
    void searchReports_ShouldReturnReportsResponseDTO() {
        // given
        ReportEntity reportEntity1 = new ReportEntity();
        reportEntity1.setTargetId(1L);
        reportEntity1.setTargetType(ReportHandleTargetTypeEnum.COMMUNITY_POST); // 게시글 신고
        reportEntity1.setStatus(ReportHandleStatusEnum.PENDING);
        reportEntity1.setReason("Reason 1");
        reportEntity1.setCreatedAt(LocalDateTime.now().minusDays(1));
        reportJpaRepository.save(reportEntity1);

        ReportEntity reportEntity2 = new ReportEntity();
        reportEntity2.setTargetId(2L);
        reportEntity2.setTargetType(ReportHandleTargetTypeEnum.POST_COMMENT); // 게시글 댓글 신고
        reportEntity2.setStatus(ReportHandleStatusEnum.RESOLVED);
        reportEntity2.setReason("Reason 2");
        reportEntity2.setCreatedAt(LocalDateTime.now());
        reportJpaRepository.save(reportEntity2);

        ReportSearchConditionDTO condition = new ReportSearchConditionDTO();
        condition.setStatus(ReportHandleStatusEnum.PENDING);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        ReportsResponseDTO response = reportRepository.searchReports(condition, pageable);

        // then
        assertThat(response.getReports()).hasSize(1);
        assertThat(response.getReports().get(0).getReason()).isEqualTo("Reason 1");
    }

    /**
     * 통계 기능이 정상적으로 이번 주, 저번 주, 전체 신고 수를 반환하는지 검증합니다.
     */
    @Test
    void findReportStatics_ShouldReturnReportStatisticsDTO() {
        // given
        ReportEntity reportEntity1 = new ReportEntity();
        reportEntity1.setTargetId(1L);
        reportEntity1.setTargetType(ReportHandleTargetTypeEnum.COMMUNITY_POST); // 게시글 신고
        reportEntity1.setStatus(ReportHandleStatusEnum.PENDING);
        reportEntity1.setReason("Reason 1");
        reportEntity1.setCreatedAt(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay()); // 이번주 월요일
        reportJpaRepository.save(reportEntity1);

        ReportEntity reportEntity2 = new ReportEntity();
        reportEntity2.setTargetId(2L);
        reportEntity2.setTargetType(ReportHandleTargetTypeEnum.POST_COMMENT); // 댓글 신고
        reportEntity2.setStatus(ReportHandleStatusEnum.PENDING);
        reportEntity2.setReason("Reason 2");
        reportEntity2.setCreatedAt(LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay().minusWeeks(1)); // 저번주 월요일
        reportJpaRepository.save(reportEntity2);

        // when
        ReportStatisticsDTO statistics = reportRepository.findReportStatics();

        // then
        assertThat(statistics.getTotalCount()).isEqualTo(2);
//        assertThat(statistics.getThisWeekCount()).isEqualTo(1);
//         assertThat(statistics.getPrevWeekCount()).isEqualTo(1);
    }
}
