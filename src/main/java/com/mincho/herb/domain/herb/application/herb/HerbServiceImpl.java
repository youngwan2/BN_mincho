package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HerbServiceImpl implements HerbService{
    private final HerbRepository herbRepository;
    private final MapperUtils mapperUtils;



    // 약초명으로 약초 찾기
    @Override
    public Herb getHerbByHerbName(String herbName) {
        HerbEntity herbEntity =  herbRepository.findByCntntsSj(herbName);
        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }
        return herbEntity.toModel();
    }


    // 약초 목록 조회(페이징)
    @Override
    public List<Herb> getHerbSummary(int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<HerbEntity> herbEntities = herbRepository.findAllPaging(pageable);

        if(herbEntities.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }

        return herbEntities.stream().map(HerbEntity::toModel).toList();
    }
    
    // 상세 페이지
    @Override
    public Herb getHerbDetails(Long id) {
        return herbRepository.findDetailsById().toModel();
    }

    // 값 초기화
    @Override
    public void insertMany() throws IOException {
        List<Herb> herbs = mapperUtils.jsonMapper("herb.json", Herb.class);
        List<HerbEntity> herbsEntity = herbs.stream().map(HerbEntity::toEntity).toList();
        herbRepository.saveAll(herbsEntity);

        log.info("mapping herb: {}",herbs.get(0));
    }
}
