package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.global.dto.PageInfoDTO;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.*;

import java.io.IOException;
import java.util.List;

public interface HerbService {

    void createHerb(HerbCreateRequestDTO herbCreateRequestDTO);
    Herb getHerbByHerbName(String herbName); // 약초 이름으로 찾기
    List<HerbDTO> getHerbs(PageInfoDTO pageInfoDTO, HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort); // 약초 목록 조회
    HerbDetailResponseDTO getHerbDetails(Long id); // 약초 상세 조회
    void removeHerb(Long id);
    void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO, Long herbId);
    void insertMany() throws IOException;
    List<HerbDTO> getRandomHerbs(Long herbId);
    List<HerbDTO> getHerbsBloomingThisMonth(String month);
    List<HerbDTO> getRecommendHerbs();
    Long getHerbCount(HerbFilteringRequestDTO herbFilteringRequestDTO);
    List<PopularityHerbsDTO> getHerbsMostview();
    List<RecommendHerbsDTO> getSimilaritySearchByRag(String question); // 코사인 유사도 검색
}
