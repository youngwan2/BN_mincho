package com.mincho.herb.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DailyPostStatisticsDTO {
    private String date; // 날짜
    private Long postCount; // 총 게시글 수
}
