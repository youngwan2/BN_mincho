package com.mincho.herb.domain.report.repository;


import com.mincho.herb.domain.report.dto.ReportMonthlyStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatusStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportTypeStatisticsDTO;
import com.mincho.herb.domain.report.entity.QReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.global.util.MathUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReportStatisticsRepositoryImpl  implements ReportStatisticsRepository {
    private final JPAQueryFactory queryFactory;

    /**
     * 신고 통계 조회(대시보드 요약)
     *
     * @return 신고 통계 DTO
     */
    @Override
    public ReportStatisticsDTO findReportStatics() {

        QReportEntity report = QReportEntity.reportEntity;

        // 현재 날짜
        LocalDate now = LocalDate.now();

        // 이번 주 월요일
        LocalDateTime startOfThisWeek = now.with(java.time.DayOfWeek.MONDAY).atStartOfDay();

        // 이번 주 일요일
        LocalDateTime endOfThisWeek = startOfThisWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        // 저번 주 월요일
        LocalDateTime startOfPrevWeek = startOfThisWeek.minusWeeks(1);

        // 저번 주 일요일
        LocalDateTime endOfPrevWeek = startOfPrevWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);

        log.info("이번주: {}, 저번주: {}", startOfThisWeek, startOfPrevWeek);

        // 이번 주 미처리 개수
        Long thisWeekCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startOfThisWeek, endOfThisWeek).and(report.status.eq(ReportHandleStatusEnum.PENDING)))
                .fetchOne();

        // 저번 주 미처리 개수
        Long prevWeekCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startOfPrevWeek, endOfPrevWeek).and(report.status.eq(ReportHandleStatusEnum.PENDING)))
                .fetchOne();

        // 미처리 신고 총 개수
        Long totalCount = queryFactory.select(report.count())
                .from(report)
                .where(report.status.eq(ReportHandleStatusEnum.PENDING))
                .fetchOne();

        // 증감율 계산
        double growthRate = MathUtil.getGrowthRate(thisWeekCount, prevWeekCount);

        return ReportStatisticsDTO.builder()
                .totalCount(totalCount)
                .thisWeekCount(thisWeekCount)
                .prevWeekCount(prevWeekCount)
                .growthRate(growthRate)
                .build();
    }

    /**
     * 월별 신고 통계 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 월별 신고 통계 DTO
     */
    @Override
    public List<ReportMonthlyStatisticsDTO> findReportMonthlyStatics(LocalDate startDate, LocalDate endDate) {
        QReportEntity report = QReportEntity.reportEntity;

        List<ReportMonthlyStatisticsDTO> result = new ArrayList<>();

        YearMonth startMonth = YearMonth.from(startDate); // 시작 월
        YearMonth endMonth = YearMonth.from(endDate); // 종료 월
        YearMonth current = startMonth; // 현재 월

        // 시작 월부터 종료 월까지 반복
        while (!current.isAfter(endMonth)) { // 현재 월이 종료 월을 넘지 않는 동안
            LocalDate monthStart = current.atDay(1);
            LocalDate monthEnd = current.atEndOfMonth();

            Long totalCount = queryFactory.select(report.count())
                    .from(report)
                    .where(report.createdAt.between(monthStart.atStartOfDay(), monthEnd.atTime(23,59,59)))
                    .fetchOne();
            Long resolvedCount = queryFactory.select(report.count())
                    .from(report)
                    .where(report.createdAt.between(monthStart.atStartOfDay(), monthEnd.atTime(23,59,59)),
                            report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.RESOLVED))
                    .fetchOne();
            Long unresolveCount = queryFactory.select(report.count())
                    .from(report)
                    .where(report.createdAt.between(monthStart.atStartOfDay(), monthEnd.atTime(23,59,59)),
                            report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.PENDING))
                    .fetchOne();
            Long rejectedCount = queryFactory.select(report.count())
                    .from(report)
                    .where(report.createdAt.between(monthStart.atStartOfDay(), monthEnd.atTime(23,59,59)),
                            report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.REJECTED))
                    .fetchOne();
            String month = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            result.add(ReportMonthlyStatisticsDTO.builder()
                    .month(month)
                    .totalCount(totalCount)
                    .resolvedCount(resolvedCount)
                    .unresolveCount(unresolveCount)
                    .rejectedCount(rejectedCount)
                    .build());
            current = current.plusMonths(1);
        }
        return result;
    }

    /**
     * 신고 이유별 통계 조회
     *
     * @param startDate 시작일
     * @param endDate   종료일
     * @return 신고 이유별 통계 DTO
     */
    @Override
    public List<ReportTypeStatisticsDTO> findReportTypeStatics(LocalDate startDate, LocalDate endDate) {
        QReportEntity report = QReportEntity.reportEntity;

        return queryFactory.select(Projections.constructor(ReportTypeStatisticsDTO.class,
                report.reasonSummary,
                report.count()
                        ))
                .from(report)
                .where(report.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23,59,59)))
                .groupBy(report.reasonSummary)
                .fetch();
    }

    /**
     * 신고 상태별 통계 조회
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @param status 신고 상태
     * @return 신고 상태별 통계 DTO
     */
    @Override
    public ReportStatusStatisticsDTO findReportStatusStatics(LocalDate startDate, LocalDate endDate, String status) {
        QReportEntity report = QReportEntity.reportEntity;
        Long totalCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23,59,59)))
                .fetchOne();
        Long unresolvedCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23,59,59)),
                        report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.PENDING))
                .fetchOne();
        Long resolvedCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23,59,59)),
                        report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.RESOLVED))
                .fetchOne();
        Long rejectedCount = queryFactory.select(report.count())
                .from(report)
                .where(report.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23,59,59)),
                        report.status.eq(com.mincho.herb.domain.report.entity.ReportHandleStatusEnum.REJECTED))
                .fetchOne();
        return ReportStatusStatisticsDTO.builder()
                .totalCount(totalCount)
                .unresolvedCount(unresolvedCount)
                .resolvedCount(resolvedCount)
                .rejectedCount(rejectedCount)
                .build();
    }
}
