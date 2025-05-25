package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import com.mincho.herb.domain.herb.dto.PopularityHerbsDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HerbRepository {
    HerbEntity findByCntntsSj(String herbName);
    HerbEntity findById(Long id);
    List<HerbEntity> findRandom(Long id1, Long id2, Long id3);
    List<HerbEntity> findAll();
    List<Long> findHerbIds();
    List<String> findHerbImagesByHerbId(Long herbId);
    List<HerbEntity> findByMonth(String month); // 이 달의 개화 약초
    List<HerbDTO> findByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO, HerbSort herbSort, Pageable pageable);
    Long countByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO); // 필터링된 약초 개수
    List<PopularityHerbsDTO> findAllByOrderByViewCountDesc();




}
