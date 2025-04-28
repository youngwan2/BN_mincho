package com.mincho.herb.domain.like.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.like.entity.HerbLikeEntity;
import com.mincho.herb.domain.like.repository.HerbLikeRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HerbLikeServiceImpl implements HerbLikeService{
    private final HerbLikeRepository herbLikeRepository;
    private final HerbRepository herbRepository;
    private final UserRepository userRepository;
    private final CommonUtils commonUtils;


    // 좋아요 추가
    @Override
    public void addHerbLike(Long herbId) {
        String email = commonUtils.userCheck();
        
        // 좋아요 존재하면 취소
        if(this.isHerbLiked(herbId)){
            this.deleteHerbLike(herbId);
        }

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다");
        }

        MemberEntity memberEntity = userRepository.findByEmail(email);
        HerbEntity herbEntity = herbRepository.findById(herbId);

        HerbLikeEntity herbLikeEntity = new HerbLikeEntity();
        herbLikeEntity.setHerb(herbEntity);
        herbLikeEntity.setMember(memberEntity);

        herbLikeRepository.insertHerbLike(herbLikeEntity);
    }

    // 좋아요 취소
    @Override
    public void deleteHerbLike(Long herbId) {
        String email = commonUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다");
        }


        MemberEntity memberEntity = userRepository.findByEmail(email);
        HerbEntity herbEntity = herbRepository.findById(herbId);

        herbLikeRepository.deleteByMemberIdAndHerbId(memberEntity.getId(), herbEntity.getId());

    }

    // 좋아요 개수 조회
    @Override
    public int countByHerbId(Long herbId) {
        return herbLikeRepository.countByHerbId(herbId);
    }

    // 좋아요 상태 체크
    @Override
    public Boolean isHerbLiked(Long herbId) {
        String email = commonUtils.userCheck();
        if(email == null){
            return false;
        }
        HerbEntity herbEntity = herbRepository.findById(herbId);
        MemberEntity memberEntity = userRepository.findByEmail(email);
        log.info("member:{}", memberEntity.getEmail());
        return herbLikeRepository.existsByMemberIdAndHerbId(memberEntity.getId(), herbEntity.getId());
    }
}
