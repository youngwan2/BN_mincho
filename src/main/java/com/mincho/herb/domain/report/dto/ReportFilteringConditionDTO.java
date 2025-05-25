package com.mincho.herb.domain.report.dto;


import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportFilteringConditionDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ReportHandleStatusEnum status;
    private ReportHandleTargetTypeEnum targetType;
}
