package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.ReportSearchConditionDTO;
import com.mincho.herb.domain.report.dto.ReportsResponseDTO;
import com.mincho.herb.domain.report.entity.ReportEntity;
import org.springframework.data.domain.Pageable;

public interface ReportRepository {
    ReportEntity save(ReportEntity reportEntity);
    ReportEntity findById(Long id);
    ReportsResponseDTO searchReports(ReportSearchConditionDTO condition, Pageable pageable);
}
