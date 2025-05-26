package com.mincho.herb.domain.report.dto;

import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSearchConditionDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ReportHandleStatusEnum status;
    private String keyword;
    private String targetType;
}