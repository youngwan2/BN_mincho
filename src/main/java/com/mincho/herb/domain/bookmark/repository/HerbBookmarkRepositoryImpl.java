package com.mincho.herb.domain.bookmark.repository;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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

    // 북마크 제거
    @Override
    public void deleteMemberIdAndHerbBookmarkId(Long memberId, Long herbBookmarkId) {
        int num = herbBookmarkJpaRepository.deleteByMemberIdAndHerbId(memberId, herbBookmarkId);

        if(num ==0){
            new CustomHttpException(HttpErrorCode.CONFLICT,"관심허브 삭제 요청이 실패하였습니다.");
        }
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

    // 사용자의 해당 약초 북마크 유뮤 체크
    @Override
    public Boolean isBookmarked(Long herbId, Long memberId) {
        if(herbBookmarkJpaRepository.findByMemberIdAndHerbId(herbId, memberId) == null){
            return false;
        }
        return true;
    }

    @Override
    public int countByMemberId(Long memberId) {
        return herbBookmarkJpaRepository.countByMemberId(memberId);
    }

    @Override
    public List<HerbBookmarkEntity> findByMemberId(Long memberId, Pageable pageable) {
        return herbBookmarkJpaRepository.findByMemberId(memberId, pageable).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"북마크 목록이 존재하지 않습니다.")).stream().toList();
    }
}
