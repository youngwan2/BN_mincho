package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DailyHerbStatisticsDTO {
    private String date; // 날짜
    private Long herbCount; // 총 게시글 수
}
