package com.mincho.herb.domain.report.dto;

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
    private Long targetId;
    private String targetType;
    private String reporter;
    private String status;
    private String reasonSummary;
    private String reason;
    private String handleTitle;
    private String handleMemo;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
