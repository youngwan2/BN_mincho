package com.mincho.herb.domain.like.application;

import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.like.dto.LikeHerbResponseDTO;
import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import com.mincho.herb.domain.like.repository.HerbLikeRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.aop.userActivity.UserActivityAction;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HerbLikeServiceImpl implements HerbLikeService{
    private final HerbLikeRepository herbLikeRepository;
    private final HerbUserQueryService herbUserQueryService;
    private final UserService userService;
    private final AuthUtils authUtils;


    // 좋아요 추가
    @Override
    @UserActivityAction(action = "herb_like")
    @Transactional
    public LikeHerbResponseDTO addHerbLike(Long herbId) {
        String email = authUtils.userCheck();
        
        // 좋아요 존재하면 취소
        if(this.isHerbLiked(herbId)){
            this.deleteHerbLike(herbId);
        }

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다");
        }

        UserEntity userEntity = userService.getUserByEmail(email);
        HerbEntity herbEntity = herbUserQueryService.getHerbById(herbId);

        HerbLikeEntity herbLikeEntity = new HerbLikeEntity();
        herbLikeEntity.setHerb(herbEntity);
        herbLikeEntity.setUser(userEntity);

        herbLikeRepository.insertHerbLike(herbLikeEntity);
        return LikeHerbResponseDTO.builder()
                .herbId(herbEntity.getId())
                .herbName(herbEntity.getCntntsSj())
                .build();
    }

    // 좋아요 취소
    @Override
    @Transactional
    public void deleteHerbLike(Long herbId) {
        String email = authUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다");
        }


        UserEntity userEntity = userService.getUserByEmail(email);
        HerbEntity herbEntity = herbUserQueryService.getHerbById(herbId);

        herbLikeRepository.deleteByMemberIdAndHerbId(userEntity.getId(), herbEntity.getId());

    }

    // 좋아요 개수 조회
    @Override
    public int countByHerbId(Long herbId) {
        return herbLikeRepository.countByHerbId(herbId);
    }

    // 좋아요 상태 체크
    @Override
    public Boolean isHerbLiked(Long herbId) {
        String email = authUtils.userCheck();
        if(email == null){
            return false;
        }
        HerbEntity herbEntity = herbUserQueryService.getHerbById(herbId);
        UserEntity userEntity = userService.getUserByEmail(email);
        log.info("user:{}", userEntity.getEmail());
        return herbLikeRepository.existsByMemberIdAndHerbId(userEntity.getId(), herbEntity.getId());
    }
}
