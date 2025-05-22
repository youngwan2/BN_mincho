package com.mincho.herb.domain.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReportStatisticsDTO {

    private Long totalCount; // 총 게시글 수
    private Long thisWeekCount; // 이번달 게시글 수
    private Long prevWeekCount; // 이전달 게시글 수
    private Double growthRate; // 증감율
}
