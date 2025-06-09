package com.mincho.herb.domain.report.application;

import com.mincho.herb.domain.report.dto.ReportMonthlyStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatusStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportTypeStatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportStatisticsService {
    List<ReportMonthlyStatisticsDTO> getMonthlyStatistics(LocalDate startDate, LocalDate endDate);
    List<ReportTypeStatisticsDTO> getTypeStatistics(LocalDate startDate, LocalDate endDate);
    ReportStatusStatisticsDTO getStatusStatistics(LocalDate startDate, LocalDate endDate, String status);
}
