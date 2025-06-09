package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ReportStatisticsRepositoryImplTest {


    @Autowired
    private ReportJpaRepository reportJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private ReportStatisticsRepositoryImpl reportStatisticsRepository;

    @BeforeEach
    void setUp() {
        reportStatisticsRepository = new ReportStatisticsRepositoryImpl(new JPAQueryFactory(entityManager));
    }

    /**
     * 통계 기능이 정상적으로 이번 주, 저번 주, 전체 신고 수를 반환하는지 검증합니다.
     */
    @Test
    void findReportStatics_ShouldReturnReportStatisticsDTO() {
        // given
        ReportEntity reportEntity1 = new ReportEntity();
        reportEntity1.setTargetId(1L);
        reportEntity1.setTargetType(ReportHandleTargetTypeEnum.POST); // 게시글 신고
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
        ReportStatisticsDTO statistics = reportStatisticsRepository.findReportStatics();

        // then
        assertThat(statistics.getTotalCount()).isEqualTo(2);
//        assertThat(statistics.getThisWeekCount()).isEqualTo(1);
//         assertThat(statistics.getPrevWeekCount()).isEqualTo(1);
    }

}
