package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HerbFilteringRequestDTO {
    private String bneNm;
    private String cntntsSj; // 약초명
    private String month;
}
