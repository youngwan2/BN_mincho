package com.mincho.herb.domain.herb.application.herbSummary;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mincho.herb.common.util.MapperUtils;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
