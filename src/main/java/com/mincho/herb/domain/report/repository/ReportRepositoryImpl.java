package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.QReportEntity;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.MathUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final ReportJpaRepository reportJpaRepository;
    private final JPAQueryFactory queryFactory;


    // 신고 생성/ 신고 처리
    @Override
    public ReportEntity save(ReportEntity reportEntity) {
        return reportJpaRepository.save(reportEntity);
    }

    // 단일 신고 조회
    @Override
    public ReportEntity findById(Long id) {
        return reportJpaRepository.findById(id).orElseThrow(()->new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 신고를 찾을 수 없습니다."));
    }

    // 신고 리스트 조회
    @Override
    @Transactional(readOnly = true)
    public ReportsResponseDTO searchReports(String keyword, ReportFilteringConditionDTO filteringConditionDTO, ReportSortDTO reportSortDTO, Pageable pageable) {
        QReportEntity report = QReportEntity.reportEntity;

        BooleanBuilder builder = new BooleanBuilder();
        LocalDateTime startDate = filteringConditionDTO.getStartDate();
        LocalDateTime endDate = filteringConditionDTO.getEndDate();
        ReportHandleStatusEnum status = filteringConditionDTO.getStatus();
        ReportHandleTargetTypeEnum targetType = filteringConditionDTO.getTargetType();
        String sort = reportSortDTO.getSort();
        String order = reportSortDTO.getOrder();

        // 신고 날짜 필터링
        if (startDate != null && endDate!= null) {
            builder.and(report.createdAt.between(startDate, endDate));
        }

        // 처리 상태 필터링
        if (status != null) {
            builder.and(report.status.eq(status));
        }

        // 신고 대상 필터링(ex. 댓글, 게시글, 유저 등)
        if (targetType != null) {
            builder.and(report.targetType.eq(targetType));
        }

        // 정렬 적용(order 가 createdAt 인 경우만 처리)

        OrderSpecifier<?> orderSpecifier = null;
        switch (sort) {
            case "createdAt" -> {
                if (order.equals("asc")) {
                    orderSpecifier = report.createdAt.asc();
                } else if (order.equals("desc")) {
                    orderSpecifier = report.createdAt.desc();
                }

                // 처리 상태
            }
            case "status" -> {
                if (order.equals("asc")) {
                    orderSpecifier = report.status.asc();
                } else if (order.equals("desc")) {
                    orderSpecifier = report.status.desc();
                }

                // 신고 대상 타입(ex. 댓글, 게시글, 유저 등)
            }
            case "targetType" -> {
                if (order.equals("asc")) {
                    orderSpecifier = report.targetType.asc();
                } else if (order.equals("desc")) {
                    orderSpecifier = report.targetType.desc();
                }

                // 신고자
            }
            case "reporter" -> {
                if (order.equals("asc")) {
                    orderSpecifier = report.reporter.email.asc();
                } else if (order.equals("desc")) {
                    orderSpecifier = report.reporter.email.desc();
                }
            }
            default -> orderSpecifier = report.id.desc(); // 기본 정렬은 ID 내림차순
        }

        // 페이징 적용
        List<ReportDTO> reports = queryFactory
                .selectFrom(report)
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(r -> ReportDTO.builder()
                        .id(r.getId())
                        .targetId(r.getTargetId())
                        .targetContentTitle(r.getTargetContentTitle())
                        .targetContentUrl(r.getTargetContentUrl())
                        .targetType(r.getTargetType().name())
                        .reporter(r.getReporter() != null ? r.getReporter().getEmail() : null)
                        .status(r.getStatus().name())
                        .reasonSummary(r.getReasonSummary())
                        .reason(r.getReason())
                        .handleTitle(r.getHandleTitle())
                        .handleMemo(r.getHandleMemo())
                        .handledAt(r.getHandledAt())
                        .createdAt(r.getCreatedAt())
                        .build()
                )
                .toList();

        // 전체 개수 조회
        long total = queryFactory
                .selectFrom(report)
                .where(builder)
                .fetch().size();


        return ReportsResponseDTO.builder()
                .reports(reports)
                .totalCount(total)
                .build();
    }


    // 신고 통계
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
}
