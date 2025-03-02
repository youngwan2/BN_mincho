package com.mincho.herb.domain.bookmark.application;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import com.mincho.herb.domain.bookmark.dto.RequestHerbBookmark;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.bookmark.repository.HerbBookmarkRepository;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HerbBookmarkServiceImpl implements HerbBookmarkService {

    private final HerbBookmarkRepository herbBookmarkRepository;
    private final CommonUtils commonUtils;
    private final UserRepository userRepository;
    private final HerbRepository herbRepository;


    // 관심 허브 추가
    @Override
    public void addHerbBookmark(String url, Long herbId) {
        String email = commonUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다.");
        }

        MemberEntity memberEntity = userRepository.findByEmail(email);
        HerbEntity herbEntity = herbRepository.findById(herbId);
        

       HerbBookmarkEntity herbBookmarkEntity = herbBookmarkRepository.findByMemberIdAndHerbId(memberEntity.getId(), herbEntity.getId());

       if(herbBookmarkEntity != null){
           throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 추가된 약초입니다.");
       }
        if(memberEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "유저 정보를 찾을 수 없습니다.");
        }

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"약초 정보를 찾을 수 없습니다.");
        }

        if(!HerbBookmark.isValidUrl(url)){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"유효한 url 형식이 아닙니다.");
        }

        herbBookmarkRepository.save(HerbBookmarkEntity.builder()
                .member(memberEntity)
                .herb(herbEntity)
                .url(url)
                .build());
    }

    // 허브 북마크 개수 조회
    @Override
    public Integer getBookmarkCount(Long herbId) {
        Integer count = herbBookmarkRepository.countByHerbId(herbId);
        return count;
    }

    // 북마크 등록 유무
    @Override
    public Boolean isBookmarked(Long herbId) {
        String email = commonUtils.userCheck();

        if(email == null){
            return false;
        }

        MemberEntity memberEntity = memberEntity = userRepository.findByEmail(email);

        return herbBookmarkRepository.findByMemberIdAndHerbId(memberEntity.getId(), herbId) != null ? true : false;

    }

    // 관심 약초 제거
    @Override
    @Transactional
    public void removeHerbBookmark(Long herbId) {

        String email = commonUtils.userCheck();
        MemberEntity memberEntity = userRepository.findByEmail(email);

        if(memberEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }
        herbBookmarkRepository.deleteMemberIdAndHerbBookmarkId(memberEntity.getId(), herbId);
    }
}
