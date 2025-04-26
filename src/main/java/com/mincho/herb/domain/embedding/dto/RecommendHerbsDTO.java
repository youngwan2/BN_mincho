package com.mincho.herb.domain.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendHerbsDTO {
    private String answer;
    private String herbName;
    private String id;
    private String priority; // 우선 순위
    private String url;
}
