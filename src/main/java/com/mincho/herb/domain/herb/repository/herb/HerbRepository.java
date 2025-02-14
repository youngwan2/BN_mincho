package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.entity.HerbEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HerbRepository {

    void saveAll(List<HerbEntity> herbs);
    Page<HerbEntity> findAllPaging(Pageable pageable);
    HerbEntity findByCntntsSj(String herbName);
    HerbEntity findDetailsById();
}
