package com.mincho.herb.domain.user.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DailyUserStatisticsDTO {
    private String date; // 날짜
    private Long userCount; // 유저 수

}
