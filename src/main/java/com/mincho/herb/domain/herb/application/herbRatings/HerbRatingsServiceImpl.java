package com.mincho.herb.domain.herb.application.herbRatings;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import com.mincho.herb.domain.herb.entity.HerbSummaryEntity;
import com.mincho.herb.domain.herb.repository.herbRatings.HerbRatingsRepository;
import com.mincho.herb.domain.herb.repository.herbSummary.HerbSummaryRepository;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HerbRatingsServiceImpl implements HerbRatingsService {

    private final HerbRatingsRepository herbRatingsRepository;
    private final HerbSummaryRepository herbSummaryRepository;
    private final UserRepository userRepository;

    @Override
    public List<HerbRatings> getHerbRatings(HerbSummary herbSummary) {
        List<HerbRatingsEntity> list = herbRatingsRepository.findAllBy(HerbSummaryEntity.toEntity(herbSummary));
        if(list.isEmpty()){
            log.info("herbRatings: {}",list);
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "평점 정보가 존재하지 않습니다.");
        }
        return list.stream().map(HerbRatingsEntity::toModel).toList();
    }

    @Override
    @Transactional
    public void addScore(HerbRatings herbRatings, String herbName, String email) {
        HerbSummaryEntity herbSummaryEntity =  herbSummaryRepository.findByCntntsSj(herbName);
        UserEntity userEntity  = userRepository.findByEmail(email);

        if(herbSummaryEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 약초 입니다.");
        }

        if(userEntity == null) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 유저 입니다.");
        }

       HerbRatingsEntity herbRatingsEntity=  HerbRatingsEntity.builder()
                .score(herbRatings.getScore())
                .member(userEntity)
                .herbSummary(herbSummaryEntity)
                .build();

        herbRatingsRepository.save(herbRatingsEntity);

    }
}