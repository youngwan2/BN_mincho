package com.mincho.herb.domain.report.api;

import com.mincho.herb.domain.report.application.ReportService;
import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "신고 생성", description = "새로운 신고를 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "신고 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping()
    public ResponseEntity<Void> createReport(
            @Valid @RequestBody CreateReportRequestDTO requestDTO
    ) {
        reportService.createReport(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "신고 처리", description = "특정 신고를 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 처리 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true))),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @PatchMapping("/{reportId}/handle")
    public ResponseEntity<Void> handleReport(
            @PathVariable Long reportId,
            @Valid  @RequestBody HandleReportRequestDTO requestDTO
    ) throws MessagingException {
        reportService.handleReport(reportId, requestDTO);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "신고 단건 조회", description = "특정 신고의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 조회 성공"),
            @ApiResponse(responseCode = "404", description = "신고를 찾을 수 없음", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("/{reportId}")
    public ResponseEntity<ReportDTO> getReport(
            @PathVariable Long reportId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getReport(reportId));
    }


    @Operation(summary = "신고 검색", description = "조건에 맞는 신고 목록을 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "신고 검색 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(hidden = true)))
    })
    @GetMapping("search")
    public ResponseEntity<ReportsResponseDTO> getAllReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @ModelAttribute ReportSortDTO reportSortDTO,
            Pageable pageable
    ) {

        ReportFilteringConditionDTO filteringConditionDTO = ReportFilteringConditionDTO.builder()
                .startDate(startDate != null ? startDate.atStartOfDay() : null)
                .endDate(endDate != null ? endDate.atTime(23, 59, 59) : null)
                .targetType(ReportHandleTargetTypeEnum.valueOf(targetType))
                .status(status != null ? ReportHandleStatusEnum.valueOf(status) : null)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(reportService.getAllReports(keyword,filteringConditionDTO, reportSortDTO, pageable));
    }
}
