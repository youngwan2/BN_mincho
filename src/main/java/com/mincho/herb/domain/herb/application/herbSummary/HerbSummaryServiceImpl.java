package com.mincho.herb.domain.herb.application.herbSummary;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HerbSummaryServiceImpl implements HerbSummaryService {
    private final HerbSummaryRepository herbSummaryRepository;
    private final MapperUtils mapperUtils;

    @Override
    public void insertMany() throws IOException {
        List<HerbSummary> herbs = mapperUtils.jsonMapper("herbSummary.json", HerbSummary.class);
        List<HerbSummaryEntity> herbsEntity = herbs.stream().map(HerbSummaryEntity::toEntity).toList();
        herbSummaryRepository.saveAll(herbsEntity);
        log.info("mapping herb: {}",herbs.get(0));

    }

  @Override
    public List<HerbSummary> getHerbs(int page, int size) {
        Pageable pageable = (Pageable) PageRequest.of(page, size);
        Page<HerbSummaryEntity> herbSummaryEntities = herbSummaryRepository.findAllPaging(pageable);
        if(herbSummaryEntities.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }
        return herbSummaryEntities.stream().map(HerbSummaryEntity::toModel).toList();
    }

    @Override
    public HerbSummary getHerbByHerbName(String herbName) {
        HerbSummaryEntity herbSummaryEntity =  herbSummaryRepository.findByCntntsSj(herbName);
        if(herbSummaryEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"조회 데이터가 없습니다.");
        }
        return herbSummaryEntity.toModel();
    }
}
