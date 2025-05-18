package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.global.dto.PageInfoDTO;

import java.util.List;

public interface HerbQueryService {
    List<HerbDTO> getRandomHerbs(Long herbId);
    List<HerbDTO> getHerbsBloomingThisMonth(String month);
    List<PopularityHerbsDTO> getHerbsMostview();
    HerbEntity getHerbByHerbName(String herbName); // 약초 이름으로 찾기
    HerbEntity getHerbById(Long id); // 약초 ID로 찾기
    List<HerbDTO> getHerbs(PageInfoDTO pageInfoDTO, HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort); // 약초 목록 조회
    HerbDetailResponseDTO getHerbDetails(Long id); // 약초 상세 조회
    Long getHerbCount(HerbFilteringRequestDTO herbFilteringRequestDTO);
}
