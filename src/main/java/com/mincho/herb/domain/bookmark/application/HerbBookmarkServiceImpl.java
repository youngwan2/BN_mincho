package com.mincho.herb.domain.bookmark.application;


import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkLogResponseDTO;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkResponseDTO;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.bookmark.repository.herbBookmark.HerbBookmarkRepository;
import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.aop.userActivity.UserActivityAction;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HerbBookmarkServiceImpl implements HerbBookmarkService {
    private final HerbBookmarkRepository herbBookmarkRepository;
    private final UserService userService;
    private final HerbUserQueryService herbUserQueryService;
    private final AuthUtils authUtils;


    // 관심약초 추가
    @Override
    @UserActivityAction(action = "herb_bookmark")

    public HerbBookmarkLogResponseDTO addHerbBookmark(String url, Long herbId) {
        String email = authUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다.");
        }

        UserEntity userEntity = userService.getUserByEmail(email);

        HerbEntity herbEntity = herbUserQueryService.getHerbById(herbId);
        

       HerbBookmarkEntity herbBookmarkEntity = herbBookmarkRepository.findByUserIdAndHerbId(userEntity.getId(), herbEntity.getId());

       if(herbBookmarkEntity != null){
           throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 추가된 약초입니다.");
       }

       if(!HerbBookmark.isValidUrl(url)){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"유효한 url 형식이 아닙니다.");
        }

        herbBookmarkRepository.save(HerbBookmarkEntity.builder()
                .user(userEntity)
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
        String email = authUtils.userCheck();

        if(email == null){
            return false;
        }

        UserEntity userEntity = userEntity = userService.getUserByEmail(email);

        return herbBookmarkRepository.findByUserIdAndHerbId(userEntity.getId(), herbId) != null;

    }

    
    // 관심약초 조회
    @Override
    public HerbBookmarkResponseDTO getBookmarks(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        String email = authUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "해당 요청에 대한 권한이 없습니다.");
        }
        UserEntity userEntity = userService.getUserByEmail(email);

        if(userEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }

        Long totalCount = herbBookmarkRepository.countByUserId(userEntity.getId()); // 전체 북마크 개수
        List<HerbBookmark> bookmarks = herbBookmarkRepository.findByUserId(userEntity.getId(), pageable)
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

    /**
     * 특정 사용자 ID의 관심 약초 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @param pageable 페이징 정보
     * @return 관심 허브 목록 응답 DTO
     */
    @Override
    @Transactional(readOnly = true)
    public HerbBookmarkResponseDTO getBookmarksByUserId(Long userId, Pageable pageable) {
        // 해당 사용자의 북마크 총 개수 조회
        Long totalCount = herbBookmarkRepository.countByUserId(userId);

        // 해당 사용자의 북마크 목록 조회
        List<HerbBookmark> bookmarks = herbBookmarkRepository.findByUserId(userId, pageable)
                .stream().map((bookmarkEntity) -> {
                    return HerbBookmark.builder()
                            .id(bookmarkEntity.getId())
                            .cntntsSj(bookmarkEntity.getHerb().getCntntsSj())
                            .bneNm(bookmarkEntity.getHerb().getBneNm())
                            .hbdcNm(bookmarkEntity.getHerb().getHbdcNm())
                            .url(bookmarkEntity.getUrl())
                            .createdAt(bookmarkEntity.getCreatedAt())
                            .build();
                }).toList();

        // 응답 DTO 구성
        return HerbBookmarkResponseDTO.builder()
                .count(totalCount)
                .bookmarks(bookmarks)
                .build();
    }

    // 관심 약초 제거
    @Override
    @Transactional
    public void removeHerbBookmark(Long herbId) {

        String email = authUtils.userCheck();
        UserEntity userEntity = userService.getUserByEmail(email);

        if(userEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"유저 정보를 찾을 수 없습니다.");
        }
        herbBookmarkRepository.deleteUserIdAndHerbBookmarkId(userEntity.getId(), herbId);
    }
}
