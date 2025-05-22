package com.mincho.herb.domain.report.entity;

import com.mincho.herb.domain.report.domain.Report;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long targetId;
    private String targetType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private UserEntity reporter; // 신고자

    private String reasonSummary;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private ReportHandleStatusEnum status;

    private LocalDateTime handledAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "handler_id")
    private UserEntity handler; // 처리자(운영자)

    private String handleTitle;
    private String handleMemo;


    // 도메인으로
    public Report toDomain() {
        return Report.builder()
                .id(id)
                .targetId(targetId)
                .targetType(targetType)
                .reporterId(reporter.getId())
                .reason(reasonSummary)
                .reason(reason)
                .status(status)
                .handledAt(handledAt)
                .handlerId(handler != null ? handler.getId() : null)
                .handleTitle(handleTitle)
                .handleMemo(handleMemo)
                .build();
    }

    // 엔티티로
    public static ReportEntity toEntity(Report report, UserEntity reporter, UserEntity handler) {
        return ReportEntity.builder()
                .id(report.getId())
                .targetId(report.getTargetId())
                .targetType(report.getTargetType())
                .reporter(reporter)
                .reasonSummary(report.getReasonSummary())
                .reason(report.getReason())
                .status(report.getStatus())
                .handledAt(report.getHandledAt())
                .handler(handler)
                .handleTitle(report.getHandleTitle())
                .handleMemo(report.getHandleMemo())
                .build();
    }
}