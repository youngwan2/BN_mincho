package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HerbBookmarkRepositoryImpl implements HerbBookmarkRepository {

    private final HerbBookmarkJpaRepository herbBookmarkJpaRepository;

    @Override
    public void save(HerbBookmarkEntity herbBookmarkEntity) {
        herbBookmarkJpaRepository.save(herbBookmarkEntity);
    }

    @Override
    public void deleteMemberIdAndHerbBookmarkId(Long memberId, Long herbBookmarkId) {
        herbBookmarkJpaRepository.deleteByMemberIdAndId(memberId, herbBookmarkId).orElseThrow(() -> new CustomHttpException(HttpErrorCode.CONFLICT,"관심허브 삭제 요청이 실패하였습니다."));
    }


    // 사용자가 추가한 해당 약초의 북마크 반환
    @Override
    public HerbBookmarkEntity findByMemberIdAndHerbId(Long memberId, Long herbId) {
        return herbBookmarkJpaRepository.findByMemberIdAndHerbId(memberId, herbId);
    }

    // 해당 약초 id 를 가진 모든 아이템의 개수를 조회
    @Override
    public Integer countByHerbId(Long herbId) {
        return herbBookmarkJpaRepository.countByHerbId(herbId);
    }
}
