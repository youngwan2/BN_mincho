package com.mincho.herb.domain.herb.application.herbDetail;

import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.HerbDetail;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbDetailEntity;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbDetail.HerbDetailRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HerbDetailServiceImpl implements HerbDetailService{

    private final HerbDetailRepository herbDetailRepository;
    private final MapperUtils mapperUtils;

    @Override
    public void insertMany() throws IOException {
        List<HerbDetail> herbs = mapperUtils.jsonMapper("herbDetail.json", HerbDetail.class);
        List<HerbDetailEntity> herbsEntity = herbs.stream().map(HerbDetailEntity::toEntity).toList();
        herbDetailRepository.saveAll(herbsEntity);
        log .info("mapping herb: {}",herbs.get(0));

    }

    @Override
    public HerbDetail getHerbDetail(String herbName) {
        return herbDetailRepository.findByCntntsSj(herbName);
    }
}
