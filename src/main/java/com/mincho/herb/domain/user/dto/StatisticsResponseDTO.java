package com.mincho.herb.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsResponseDTO {
    private Long commentCount;
    private Long postCount;
    private Long bookmarkCount;
}
