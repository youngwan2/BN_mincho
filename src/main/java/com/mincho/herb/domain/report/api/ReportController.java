package com.mincho.herb.domain.report.api;

import com.mincho.herb.domain.report.application.ReportService;
import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reports")
public class ReportController {
    private final ReportService reportService;


    // 신고하기
    @PostMapping()
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody CreateReportRequestDTO requestDTO
    ) {
        reportService.createReport(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 신고 처리하기
    @PatchMapping("/{reportId}/handle")
    public ResponseEntity<Void> handleReport(
            @PathVariable Long reportId,
            @Valid  @RequestBody HandleReportRequestDTO requestDTO
    ) throws MessagingException {
        reportService.handleReport(reportId, requestDTO);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 신고 단건 조회
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReport(reportId));
    }




    /**
     * 신고 검색 API
     *
     * @param startDate   검색 시작 날짜 (yyyy-MM-dd)
     * @param endDate     검색 종료 날짜 (yyyy-MM-dd)
     * @param targetType  대상 타입 (예: "POST", "COMMENT")
     * @param reporter    신고자 닉네임
     * @param status      신고 상태 (예: "PENDING", "RESOLVED")
     * @param pageable    페이징 정보
     * @return 조건에 맞는 신고 목록 페이지
     */
    @GetMapping("search")
    public ResponseEntity<ReportsResponseDTO> getAllReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String reporter,
            @RequestParam(required = false) String status,
            Pageable pageable
    ) {

        ReportSearchConditionDTO searchConditionDTO = ReportSearchConditionDTO.builder()
                .targetType(targetType)
                .status(ReportHandleStatusEnum.valueOf(status))
                .startDate(startDate != null ? startDate.atStartOfDay() : null)
                .endDate(endDate != null ? endDate.atTime(23, 59, 59) : null)
                .reporter(reporter)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getAllReports(searchConditionDTO, pageable));
    }
}

