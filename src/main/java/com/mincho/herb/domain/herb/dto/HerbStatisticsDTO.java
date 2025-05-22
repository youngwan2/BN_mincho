package com.mincho.herb.domain.herb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HerbStatisticsDTO {
    private Long totalCount; // 총 게시글 수
    private Long currentMonthCount; // 이번달 게시글 수
    private Long previousMonthCount; // 이전달 게시글 수
    private Double growthRate; // 증감율
}
