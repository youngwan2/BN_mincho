package com.mincho.herb.domain.report.repository;

import com.mincho.herb.domain.report.dto.ReportMonthlyStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatusStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportTypeStatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportStatisticsRepository {
    ReportStatisticsDTO findReportStatics();
    List<ReportMonthlyStatisticsDTO> findReportMonthlyStatics(LocalDate startDate, LocalDate endDate);
    List<ReportTypeStatisticsDTO> findReportTypeStatics(LocalDate startDate, LocalDate endDate);
    ReportStatusStatisticsDTO findReportStatusStatics(LocalDate startDate, LocalDate endDate, String status);
}
