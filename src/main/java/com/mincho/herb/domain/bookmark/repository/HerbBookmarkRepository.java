package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;

import java.util.List;

public interface HerbBookmarkRepository {

    void save(HerbBookmarkEntity herbBookmarkEntity);
    void deleteMemberIdAndHerbBookmarkId(Long memberId, Long herbBookmarkId);
    HerbBookmarkEntity findByMemberIdAndHerbId(Long memberId, Long herbId);
    Integer countByHerbId(Long herbId);
    Boolean isBookmarked(Long herbId, Long memberId);
}
