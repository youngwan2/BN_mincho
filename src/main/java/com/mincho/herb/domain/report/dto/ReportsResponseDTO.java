package com.mincho.herb.domain.report.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportsResponseDTO {

    private List<ReportDTO> reports;
    private Long totalCount;
}
