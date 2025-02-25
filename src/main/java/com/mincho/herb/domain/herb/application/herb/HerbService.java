package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbDTO;
import com.mincho.herb.domain.herb.dto.HerbDetailResponseDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;

import java.io.IOException;
import java.util.List;

public interface HerbService {

    void createHerb(HerbCreateRequestDTO herbCreateRequestDTO);
    Herb getHerbByHerbName(String herbName); // 약초 이름으로 찾기
    List<Herb> getHerbSummary(int page, int size); // 약초 목록 조회
    HerbDetailResponseDTO getHerbDetails(Long id); // 약초 상세 조회
    void removeHerb(Long id);
    void updateHerb(HerbUpdateRequestDTO herbUpdateRequestDTO, Long herbId);
    void insertMany() throws IOException;
    List<HerbDTO> getRandomHerbs(Long herbId);
    List<HerbDTO> getHerbsBloomingThisMonth(String month);
}
