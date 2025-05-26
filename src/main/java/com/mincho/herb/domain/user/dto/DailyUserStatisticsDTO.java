package com.mincho.herb.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DailyUserStatisticsDTO {
    private String date; // 날짜
    private Long userCount; // 유저 수

}
