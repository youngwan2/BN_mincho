package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class HerbRepositoryImpl implements HerbRepository {

    private final HerbJpaRepository herbJpaRepository;

    @Override
    public void save(HerbEntity herbEntity) {
        herbJpaRepository.save(herbEntity);
    }

    @Override
    public void saveAll(List<HerbEntity> herbs) {
        herbJpaRepository.saveAll(herbs);
    }

    @Override
    public Page<HerbEntity> findAllPaging(Pageable pageable) {
        return herbJpaRepository.findAll(pageable);
    }

    @Override
    public HerbEntity findByCntntsSj(String herbName) {
        return herbJpaRepository.findByCntntsSj(herbName);
    }

    @Override
    public HerbEntity findById(Long id) {
        return herbJpaRepository.findById(id).orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"해당 약초 데이터가 존재하지 않습니다."));
    }

    @Override
    public void deleteById(Long id) {
        herbJpaRepository.deleteById(id);
    }

    @Override
    public List<HerbEntity> findRandom(Long id1, Long id2, Long id3) {
        log.info("id1:{}, id2:{}, id3:{}", id2, id2, id3);
        return  herbJpaRepository.findRandom(id1, id2, id3).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 데이터 목록이 존재하지 않습니다."));
    }

    @Override
    public List<Long> findHerbIds() {
        return herbJpaRepository.findHerbIds().orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 데이터의 id 목록이 존재하지 않습니다."));
    }

    @Override
    public List<HerbEntity> findByMonth(String month) {
        return herbJpaRepository.findByMonth(month).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 목록이 존재하지 않습니다."));
    }
}
