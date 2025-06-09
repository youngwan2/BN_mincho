package com.mincho.herb.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReportMonthlyStatisticsDTO {
    private String month; // 월 (예: "2023-10")
    private Long totalCount; // 총 신고 수
    private Long resolvedCount; // 처리된 신고 수
    private Long unresolveCount; // 미처리 신고 수
    private Long rejectedCount; // 반려된 신고 수
}
