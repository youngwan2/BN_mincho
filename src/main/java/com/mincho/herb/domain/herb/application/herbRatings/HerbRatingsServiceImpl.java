package com.mincho.herb.domain.herb.application.herbRatings;

import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.HerbRatingsEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.herb.repository.herbRatings.HerbRatingsRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
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
    private final HerbRepository herbRepository;
    private final UserService userService;

    @Override
    public List<HerbRatings> getHerbRatings(HerbEntity herb) {
        List<HerbRatingsEntity> list = herbRatingsRepository.findAllBy(herb);
        if(list.isEmpty()){
            log.info("herbRatings: {}",list);
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "평점 정보가 존재하지 않습니다.");
        }
        return list.stream().map(HerbRatingsEntity::toModel).toList();
    }

    @Override
    @Transactional
    public void addScore(HerbRatings herbRatings, String herbName, String email) {
        HerbEntity herbEntity =  herbRepository.findByCntntsSj(herbName);
        UserEntity userEntity = userService.getUserByEmail(email);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 약초 입니다.");
        }

        if(userEntity == null) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "존재하지 않는 유저 입니다.");
        }
        if(!herbRatings.isScoreValid()){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"평점은 0 ~ 5점 사이 이어야 합니다.");

        }
       HerbRatingsEntity herbRatingsEntity=  HerbRatingsEntity.builder()
                .score(herbRatings.getScore())
                .user(userEntity)
                .herb(herbEntity)
                .build();

        herbRatingsRepository.save(herbRatingsEntity);

    }
}