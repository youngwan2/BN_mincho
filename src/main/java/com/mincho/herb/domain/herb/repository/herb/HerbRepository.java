package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.global.page.PageInfoDTO;

import java.util.List;

public interface HerbRepository {
    HerbEntity save(HerbEntity herbEntity);
    void saveAll(List<HerbEntity> herbs);
    HerbEntity findByCntntsSj(String herbName);
    HerbEntity findById(Long id);
    void deleteById(Long id);
    List<HerbEntity> findRandom(Long id1, Long id2, Long id3);
    List<HerbEntity> findAll();
    List<Long> findHerbIds();
    List<HerbEntity> findByMonth(String month); // 이 달의 개화 약초
    List<HerbDTO> findByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort, PageInfoDTO pageInfoDTO);
    Long countByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO); // 필터링된 약초 개수
    List<PopularityHerbsDTO> findAllByOrderByViewCountDesc();

    HerbEntity removeHerbImagesByHerbId(Long herbId);

    HerbStatisticsDTO findHerbStatics();
}
