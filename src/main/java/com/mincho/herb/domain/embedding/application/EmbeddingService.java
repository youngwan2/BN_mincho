package com.mincho.herb.domain.embedding.application;

import com.mincho.herb.domain.embedding.dto.HerbEmbeddingDTO;

import java.util.List;

public interface EmbeddingService {
    void embedAllHerbsToPgVector(); // 약초 정보 벡터 변환
    List<HerbEmbeddingDTO>  getAllHerbsEmbedding(); // 약초 정보 벡터 조회
}
