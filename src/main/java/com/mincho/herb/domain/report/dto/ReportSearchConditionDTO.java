package com.mincho.herb.domain.report.dto;

import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportSearchConditionDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ReportHandleStatusEnum status;
    private String reporter;
    private String targetType;
}