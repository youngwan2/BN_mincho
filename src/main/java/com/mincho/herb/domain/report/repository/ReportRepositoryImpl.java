package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.ReportDTO;
import com.mincho.herb.domain.report.dto.ReportSearchConditionDTO;
import com.mincho.herb.domain.report.dto.ReportsResponseDTO;
import com.mincho.herb.domain.report.entity.QReportEntity;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    public ReportsResponseDTO searchReports(ReportSearchConditionDTO condition, Pageable pageable) {
        QReportEntity report = QReportEntity.reportEntity;

        BooleanBuilder builder = new BooleanBuilder();

        // 신고 날짜 필터링
        if (condition.getStartDate() != null && condition.getEndDate() != null) {
            builder.and(report.createdAt.between(condition.getStartDate(), condition.getEndDate()));
        }

        // 처리 상태 필터링
        if (condition.getStatus() != null) {
            builder.and(report.status.eq(condition.getStatus()));
        }

        // 신고 대상 필터링(ex. 댓글, 게시글, 유저 등)
        if (condition.getTargetType() != null) {
            builder.and(report.targetType.eq(condition.getTargetType()));
        }

        // 조회 쿼리
        JPAQuery<ReportEntity> query = queryFactory
                .selectFrom(report)
                .where(builder);

        // 정렬 적용(order 가 createdAt 인 경우만 처리)
        for (Sort.Order order : pageable.getSort()) {
            if (order.getProperty().equals("createdAt")) {
                query.orderBy(order.isAscending()
                        ? report.createdAt.asc()  // 오래된 순
                        : report.createdAt.desc() // 최신 순
                );
            }
        }

        // 페이징 적용
        List<ReportDTO> reports = queryFactory
                .selectFrom(report)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(r -> ReportDTO.builder()
                        .id(r.getId())
                        .targetId(r.getTargetId())
                        .targetType(r.getTargetType())
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
}
