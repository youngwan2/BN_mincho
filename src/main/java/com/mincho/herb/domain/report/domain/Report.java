package com.mincho.herb.domain.report.domain;

import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import com.mincho.herb.domain.report.entity.ReportResonSummaryEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    private Long id;
    private Long targetId;
    private ReportHandleTargetTypeEnum targetType;
    private Long reporterId;
    private ReportResonSummaryEnum reasonSummary;
    private String reason;
    private ReportHandleStatusEnum status; // PENDING, RESOLVED, CANCELLED
    private LocalDateTime handledAt;
    private Long handlerId;
    private String handleTitle;
    private String handleMemo;

    // 처리 완료
    public void resolve(Long adminId, String title, String description) {
        this.status = ReportHandleStatusEnum.RESOLVED;
        this.handledAt = LocalDateTime.now();
        this.handlerId = adminId;
        this.handleTitle = title;
        this.handleMemo = description;
    }

    // 처리 취소
    public void cancel(Long adminId) {
        this.status =  ReportHandleStatusEnum.REJECTED;
        this.handledAt = LocalDateTime.now();
        this.handlerId = adminId;
    }

    // 처리 유무
    public boolean isResolved() {
        return "처리완료".equals(this.status.getDescription());
    }
}
