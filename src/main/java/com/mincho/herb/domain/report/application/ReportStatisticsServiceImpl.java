package com.mincho.herb.domain.report.application;

import com.mincho.herb.domain.report.dto.ReportMonthlyStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatusStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportTypeStatisticsDTO;
import com.mincho.herb.domain.report.repository.ReportStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportStatisticsServiceImpl implements ReportStatisticsService{
    private final ReportStatisticsRepository reportStatisticsRepository;

    @Override
    public List<ReportMonthlyStatisticsDTO> getMonthlyStatistics(LocalDate startDate, LocalDate endDate) {
        return reportStatisticsRepository.findReportMonthlyStatics(startDate, endDate);
    }

    @Override
    public List<ReportTypeStatisticsDTO> getTypeStatistics(LocalDate startDate, LocalDate endDate) {
        return reportStatisticsRepository.findReportTypeStatics(startDate, endDate);
    }

    @Override
    public ReportStatusStatisticsDTO getStatusStatistics(LocalDate startDate, LocalDate endDate, String status) {
        return reportStatisticsRepository.findReportStatusStatics(startDate, endDate, status);
    }
}
