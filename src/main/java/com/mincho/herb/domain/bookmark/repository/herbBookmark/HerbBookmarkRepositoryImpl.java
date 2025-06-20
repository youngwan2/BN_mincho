package com.mincho.herb.domain.bookmark.repository.herbBookmark;

import com.mincho.herb.domain.bookmark.entity.HerbBookmarkEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
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
    public void deleteUserIdAndHerbBookmarkId(Long userId, Long herbBookmarkId) {
        int num = herbBookmarkJpaRepository.deleteByUserIdAndHerbId(userId, herbBookmarkId);

        if(num ==0){
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "관심허브 삭제 요청이 실패하였습니다.");
        }
    }


    // 사용자가 추가한 해당 약초의 북마크 반환
    @Override
    public HerbBookmarkEntity findByUserIdAndHerbId(Long userId, Long herbId) {
        return herbBookmarkJpaRepository.findByUserIdAndHerbId(userId, herbId);
    }

    // 해당 약초 id 를 가진 모든 아이템의 개수를 조회
    @Override
    public Long countByHerbId(Long herbId) {
        return herbBookmarkJpaRepository.countByHerbId(herbId);
    }

    // 사용자의 해당 약초 북마크 유뮤 체크
    @Override
    public Boolean isBookmarked(Long herbId, Long userId) {
        if(herbBookmarkJpaRepository.findByUserIdAndHerbId(herbId, userId) == null){
            return false;
        }
        return true;
    }

    @Override
    public Long countByUserId(Long userId) {
        return herbBookmarkJpaRepository.countByUserId(userId);
    }

    @Override
    public List<HerbBookmarkEntity> findByUserId(Long userId, Pageable pageable) {
        return herbBookmarkJpaRepository.findByUserId(userId, pageable).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND,"북마크 목록이 존재하지 않습니다.")).stream().toList();
    }
    // 유저 북마크 전체 삭제
    @Override
    public void deleteByUser(UserEntity userEntity) {
        herbBookmarkJpaRepository.deleteByUser(userEntity);
    }
}
