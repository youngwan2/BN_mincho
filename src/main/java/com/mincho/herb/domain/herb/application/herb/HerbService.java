package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.domain.Herb;

import java.io.IOException;
import java.util.List;

public interface HerbService {
    void insertMany() throws IOException;

    Herb getHerbByHerbName(String herbName); // 약초 이름으로 찾기
    List<Herb> getHerbSummary(int page, int size); // 약초 목록 조회
    Herb getHerbDetails(Long id); // 약초 상세 조회
}
