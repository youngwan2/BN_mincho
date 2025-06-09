package com.mincho.herb.domain.bookmark.repository.postBookmark;

import com.mincho.herb.domain.bookmark.entity.PostBookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBookmarkJpaRepository extends JpaRepository<PostBookmarkEntity, Long> {

    // 사용자 ID와 게시글 ID로 북마크 삭제
    void deleteByUserIdAndPostId(Long userId, Long postId);

    // 사용자 ID와 게시글 ID로 북마크 조회
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // 사용자의 북마크 수 조회
    Long countByUserId(Long userId);
}
