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
public class HerbFilteringConditionDTO {
    private String bneNm; // 학명(카테고리)
    private String tagType; // 태그 타입
    private List<String> tag; // 효능

}
