package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HerbFilteringRequestDTO {
    private String bneNm; // 학명(카테고리)
    private String cntntsSj; // 약초명
    private String month;
    private String tagType; // 태그 타입 (효능, 부작용 등)
    private List<String> tags;
}
