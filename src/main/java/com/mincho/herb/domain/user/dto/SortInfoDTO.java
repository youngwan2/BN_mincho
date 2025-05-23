package com.mincho.herb.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SortInfoDTO {
    private String sort; // 정렬 기준
    private String order; // 정렬 순서(asc, desc)
}
