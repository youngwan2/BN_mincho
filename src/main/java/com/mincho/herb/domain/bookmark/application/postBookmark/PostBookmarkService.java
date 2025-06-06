package com.mincho.herb.domain.bookmark.application.postBookmark;

import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkResponseDTO;
import org.springframework.data.domain.Pageable;

public interface PostBookmarkService {

    /**
     * 게시글 북마크 추가
     *
     * @param postId 게시글 ID
     * @param email 사용자 이메일
     * @return 북마크 추가 여부
     */
    boolean addBookmark(Long postId, String email);

    /**
     * 게시글 북마크 삭제
     *
     * @param postId 게시글 ID
     * @param email 사용자 이메일
     * @return 북마크 삭제 여부
     */
    boolean removeBookmark(Long postId, String email);

    /**
     * 게시글 북마크 상태 확인
     *
     * @param postId 게시글 ID
     * @param email 사용자 이메일
     * @return 북마크 여부
     */
    boolean isBookmarked(Long postId, String email);

    /**
     * 사용자의 북마크한 게시글 목록 조회
     *
     * @param email 사용자 이메일
     * @param pageable 페이지 정보
     * @return 북마크한 게시글 목록
     */
    PostBookmarkResponseDTO getBookmarkedPosts(String email, Pageable pageable);
}
