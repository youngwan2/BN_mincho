package com.mincho.herb.domain.embedding.application;

import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;

import java.util.List;

public interface EmbeddingService {

    List<RecommendHerbsDTO> similaritySearch(String search);
    void embedAllHerbsToPgVector();

}
