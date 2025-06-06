package com.mincho.herb.domain.report.api;

import com.mincho.herb.domain.report.application.ReportStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/reports/statistics")
public class ReportStatisticsController {

    private final ReportStatisticsService reportStatisticsService;

    @Operation(summary = "처리상태별 신고 통계 조회", description = "관리자가 처리상태별로 신고 통계를 조회합니다.")
    @GetMapping("/status")
    public ResponseEntity<?> getReportStatisticsByStatus(
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(reportStatisticsService.getStatusStatistics(startDate, endDate, status));
    }

    @Operation(summary ="신고 유형별 신고 통계 조회", description = "관리자가 신고유형별로 신고 통계를 조회합니다.")
    @GetMapping("/type")
    public ResponseEntity<?> getReportStatisticsByType(
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return ResponseEntity.ok(reportStatisticsService.getTypeStatistics(startDate, endDate));
    }

    @Operation(summary = "월별 신고 통계 조회", description = "관리자가 월별로 신고 통계를 조회합니다.")
    @GetMapping("/monthly")
    public ResponseEntity<?> getReportStatisticsByPeriod(
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = true)  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ) {
        return ResponseEntity.ok(reportStatisticsService.getMonthlyStatistics(startDate, endDate));
    }
}

// 조회할 것
// => 기간별 조회(예: 지난 7일, 지난 30일 등)
// - 신고 총 건수
// - 신고 처리 완료 건수
// - 신고 처리 중 건수
// - 신고 처리 반려 건수
// - 각 건수별 비율

