package com.mincho.herb.domain.bookmark.application;


import com.mincho.herb.domain.bookmark.dto.HerbBookmarkLogResponseDTO;
import com.mincho.herb.global.aop.UserActivityAction;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import com.mincho.herb.domain.bookmark.dto.HerbBookmarkResponseDTO;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.bookmark.repository.HerbBookmarkRepository;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HerbBookmarkServiceImpl implements HerbBookmarkService {

    private final HerbBookmarkRepository herbBookmarkRepository;
    private final CommonUtils commonUtils;
    private final UserRepository userRepository;
    private final HerbRepository herbRepository;


    // 관심약초 추가
    @Override
    @UserActivityAction(action = "herb_bookmark")

    public HerbBookmarkLogResponseDTO addHerbBookmark(String url, Long herbId) {
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

       if(!HerbBookmark.isValidUrl(url)){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"유효한 url 형식이 아닙니다.");
        }

        herbBookmarkRepository.save(HerbBookmarkEntity.builder()
                .member(memberEntity)
                .herb(herbEntity)
                .url(url)
                .build());

       return HerbBookmarkLogResponseDTO.builder()
               .herbId(herbId)
               .herbName(herbEntity.getCntntsSj())
               .build();
    }

    // 관심약초 개수 조회(약초별)
    @Override
    public Long getBookmarkCount(Long herbId) {
        return herbBookmarkRepository.countByHerbId(herbId);
    }


    // 관심약초 등록 유무
    @Override
    public Boolean isBookmarked(Long herbId) {
        String email = commonUtils.userCheck();

        if(email == null){
            return false;
        }

        MemberEntity memberEntity = memberEntity = userRepository.findByEmail(email);

        return herbBookmarkRepository.findByMemberIdAndHerbId(memberEntity.getId(), herbId) != null;

    }

    
    // 관심약초 조회
    @Override
    public HerbBookmarkResponseDTO getBookmarks(int page, int size) {

        Pageable pageable = (Pageable) PageRequest.of(page, size);

        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다.");
        }
        MemberEntity memberEntity = userRepository.findByEmail(email);

        if(memberEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }

        Long totalCount = herbBookmarkRepository.countByMemberId(memberEntity.getId()); // 전체 북마크 개수
        List<HerbBookmark> bookmarks = herbBookmarkRepository.findByMemberId(memberEntity.getId(), pageable)
                        .stream().map((bookmarkEntity)->{
                            
                            return HerbBookmark.builder()
                                    .id(bookmarkEntity.getId())
                                    .cntntsSj(bookmarkEntity.getHerb().getCntntsSj())
                                    .bneNm(bookmarkEntity.getHerb().getBneNm())
                                    .hbdcNm(bookmarkEntity.getHerb().getHbdcNm())
                                    .url(bookmarkEntity.getUrl())
                                    .createdAt(bookmarkEntity.getCreatedAt())
                                    .build();
                }).toList();



        return HerbBookmarkResponseDTO.builder()
                .count(totalCount)
                .bookmarks(bookmarks)
                .build();
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
