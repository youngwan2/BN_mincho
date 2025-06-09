package com.mincho.herb.domain.report.dto;

import com.mincho.herb.domain.report.entity.ReportReasonSummaryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReportTypeStatisticsDTO {
    private ReportReasonSummaryEnum reasonSummary;
    private Long count;
}
