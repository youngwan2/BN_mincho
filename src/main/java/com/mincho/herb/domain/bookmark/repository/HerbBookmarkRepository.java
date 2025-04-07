package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface HerbBookmarkRepository {

    void save(HerbBookmarkEntity herbBookmarkEntity);
    void deleteMemberIdAndHerbBookmarkId(Long memberId, Long herbBookmarkId);
    HerbBookmarkEntity findByMemberIdAndHerbId(Long memberId, Long herbId);

    // 약초 당 북마크 개수
    Long countByHerbId(Long herbId);

    // 북마크 상태
    Boolean isBookmarked(Long herbId, Long memberId);

    // 유저의 북마크 개수 조회
    Long countByMemberId(Long memberId);

    // 유저의 북마크 목록 조회
    List<HerbBookmarkEntity> findByMemberId(Long memberId, Pageable pageable);

}
