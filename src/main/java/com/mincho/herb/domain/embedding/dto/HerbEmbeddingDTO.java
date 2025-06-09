package com.mincho.herb.domain.embedding.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HerbEmbeddingDTO {
    private Long herbId;
    private List<Double> embedding;
}
