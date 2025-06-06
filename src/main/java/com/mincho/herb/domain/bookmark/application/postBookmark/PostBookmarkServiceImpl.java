package com.mincho.herb.domain.bookmark.application.postBookmark;

import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkResponseDTO;
import com.mincho.herb.domain.bookmark.entity.PostBookmarkEntity;
import com.mincho.herb.domain.bookmark.repository.postBookmark.PostBookmarkRepository;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostBookmarkServiceImpl implements PostBookmarkService {

    private final PostBookmarkRepository postBookmarkRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    @Override
    @Transactional
    public boolean addBookmark(Long postId, String email) {
        // 사용자 조회
        UserEntity user = userService.getUserByEmail(email);

        // 게시글 조회
        PostEntity post = postRepository.findByIdAndIsDeletedFalse(postId);

        // 이미 북마크한 게시글인지 확인
        boolean isBookmarked = postBookmarkRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (isBookmarked) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 북마크한 게시글입니다.");
        }

        // 북마크 추가
        PostBookmarkEntity postBookmark = PostBookmarkEntity.of(user, post);
        postBookmarkRepository.save(postBookmark);

        return true;
    }

    @Override
    @Transactional
    public boolean removeBookmark(Long postId, String email) {
        // 사용자 조회
        UserEntity user = userService.getUserByEmail(email);

        // 북마크 존재 확인
        boolean isBookmarked = postBookmarkRepository.existsByUserIdAndPostId(user.getId(), postId);
        if (!isBookmarked) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "북마크하지 않은 게시글입니다.");
        }

        // 북마크 삭제
        postBookmarkRepository.deleteByUserIdAndPostId(user.getId(), postId);

        return true;
    }

    @Override
    public boolean isBookmarked(Long postId, String email) {
        // 사용자 조회
        UserEntity user = userService.getUserByEmailOrNull(email);

        // 로그인하지 않은 상태면 북마크하지 않은 것으로 처리
        if (user == null) {
            return false;
        }

        // 북마크 여부 반환
        return postBookmarkRepository.existsByUserIdAndPostId(user.getId(), postId);
    }

    @Override
    public PostBookmarkResponseDTO getBookmarkedPosts(String email, Pageable pageable) {
        // 사용자 조회
        UserEntity user = userService.getUserByEmail(email);

        // 북마크한 게시글 목록 반환
        return postBookmarkRepository.findBookmarkedPostsByUserId(user.getId(), pageable);
    }
}
