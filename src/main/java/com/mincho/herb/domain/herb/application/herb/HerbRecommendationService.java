package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.domain.herb.dto.HerbDTO;

import java.util.List;

public interface HerbRecommendationService {
    List<HerbDTO> getRecommendHerbs();
    List<RecommendHerbsDTO> getSimilaritySearchByRag(String question); // 코사인 유사도 검색
}
