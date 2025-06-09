package com.mincho.herb.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReportStatusStatisticsDTO {
    private Long totalCount;
    private Long unresolvedCount;
    private Long resolvedCount;
    private Long rejectedCount;
}
