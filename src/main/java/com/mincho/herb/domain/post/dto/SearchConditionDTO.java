package com.mincho.herb.domain.post.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchConditionDTO {
    private String query;
    private String queryType;
    private String sort;
    private String order;
    private Long categoryId;
    private String tag;  // 태그로 검색할 때 사용
}
