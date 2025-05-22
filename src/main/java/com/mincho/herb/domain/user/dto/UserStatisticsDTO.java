package com.mincho.herb.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserStatisticsDTO {
    private Long totalCount; // 총 유저 수
    private Long currentMonthCount; // 이번달 가입자수
    private Long previousMonthCount; // 이전달 가입자수
    private Double growthRate; // 증감율

}
