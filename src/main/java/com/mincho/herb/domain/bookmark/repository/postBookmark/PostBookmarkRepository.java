package com.mincho.herb.domain.bookmark.repository.postBookmark;

import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkResponseDTO;
import com.mincho.herb.domain.bookmark.entity.PostBookmarkEntity;
import org.springframework.data.domain.Pageable;

public interface PostBookmarkRepository {

    // 북마크 저장
    PostBookmarkEntity save(PostBookmarkEntity postBookmarkEntity);

    // 북마크 삭제
    void deleteByUserIdAndPostId(Long userId, Long postId);

    // 사용자 ID와 게시글 ID로 북마크 조회
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // 사용자가 북마크한 게시글 목록 조회
    PostBookmarkResponseDTO findBookmarkedPostsByUserId(Long userId, Pageable pageable);

    // 사용자의 북마크 수 조회
    Long countByUserId(Long userId);
}
