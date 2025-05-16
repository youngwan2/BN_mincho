package com.mincho.herb.domain.report.application;

import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.ReportEntity;
import org.springframework.data.domain.Pageable;


public interface ReportService {
    ReportEntity createReport(CreateReportRequestDTO requestDTO);
    // 신고 단건 조회
    ReportDTO getReport(Long id);

    // 신고 처리 (상태 변경, 처리자 지정 등)
    void handleReport(Long reportId, HandleReportRequestDTO requestDTO);

    // 전체 신고 리스트 조회 (필터링, 페이징 추가)
    ReportsResponseDTO getAllReports(ReportSearchConditionDTO searchConditionDTO, Pageable pageable);

}
