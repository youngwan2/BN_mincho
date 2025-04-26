package com.mincho.herb.domain.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecommendHerbResponseDTO {

    private String sender;
    private List<RecommendHerbsDTO> recommendHerbs;
    private String createdAt;
}
