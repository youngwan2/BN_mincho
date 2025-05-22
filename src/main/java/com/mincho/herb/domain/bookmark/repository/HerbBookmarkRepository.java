package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HerbBookmarkRepository {

    void save(HerbBookmarkEntity herbBookmarkEntity);
    void deleteUserIdAndHerbBookmarkId(Long userId, Long herbBookmarkId);
    HerbBookmarkEntity findByUserIdAndHerbId(Long userId, Long herbId);

    // 약초 당 북마크 개수
    Long countByHerbId(Long herbId);

    // 북마크 상태
    Boolean isBookmarked(Long herbId, Long UserId);

    // 유저의 북마크 개수 조회
    Long countByUserId(Long userId);

    // 유저의 북마크 목록 조회
    List<HerbBookmarkEntity> findByUserId(Long userId, Pageable pageable);

    // 유저 북마크 전체 삭제
    void deleteByUser(UserEntity userEntity);

}
