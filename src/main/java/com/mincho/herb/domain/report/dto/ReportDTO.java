package com.mincho.herb.domain.report.dto;

import com.mincho.herb.domain.report.entity.ReportReasonSummaryEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 신고 정보를 담는 DTO(Data Transfer Object) 클래스입니다.
 * <p>
 * 프레젠테이션 계층과 서비스 계층 간에 신고 관련 데이터를 전달할 때 사용됩니다.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDTO {
    private Long id;
    private Long targetId; // 신고 대상의 ID
    private String targetContentTitle; // 신고 대상의 제목
    private String targetContentUrl; // 신고 대상의 URL
    private String targetType; // 신고 대상의 타입 (예: 사용자, 게시물 등)
    private String reporter;
    private String status;
    private ReportReasonSummaryEnum reasonSummary;
    private String reason;
    private String handleTitle;
    private String handleMemo;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
