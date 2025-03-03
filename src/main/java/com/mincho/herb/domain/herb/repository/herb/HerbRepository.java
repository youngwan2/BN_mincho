package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringRequestDTO;
import com.mincho.herb.domain.herb.dto.PageInfoDTO;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HerbRepository {
    void save(HerbEntity herbEntity);
    void saveAll(List<HerbEntity> herbs);
    HerbEntity findByCntntsSj(String herbName);
    HerbEntity findById(Long id);
    void deleteById(Long id);
    List<HerbEntity> findRandom(Long id1, Long id2, Long id3);
    List<Long> findHerbIds();
    List<HerbEntity> findByMonth(String month); // 이 달의 개화 약초
    List<HerbDTO> findByFiltering(HerbFilteringRequestDTO herbFilteringRequestDTO, PageInfoDTO pageInfoDTO);
}
