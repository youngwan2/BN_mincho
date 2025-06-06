package com.mincho.herb.domain.bookmark.api;

import com.mincho.herb.domain.bookmark.application.postBookmark.PostBookmarkService;
import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkResponseDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
@Tag(name = "Post Bookmark", description = "게시글 북마크 관련 API")
public class PostBookmarkController {

    private final PostBookmarkService postBookmarkService;

    @PostMapping("/users/me/posts/{postId}/post-bookmarks")
    @Operation(summary = "게시글 북마크 추가", description = "특정 게시글을 북마크에 추가합니다.")
    public ResponseEntity<?> addBookmark(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postBookmarkService.addBookmark(postId, email);
        return ResponseEntity.status(201).build();

    }

    @DeleteMapping("/users/me/posts/{postId}/post-bookmarks")
    @Operation(summary = "게시글 북마크 삭제", description = "특정 게시글을 북마크에서 제거합니다.")
    public ResponseEntity<?> removeBookmark(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postBookmarkService.removeBookmark(postId, email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/me/posts/{postId}/post-bookmarks")
    @Operation(summary = "게시글 북마크 상태 확인", description = "특정 게시글의 북마크 상태를 확인합니다.")
    public ResponseEntity<?> isBookmarked(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        boolean isBookmarked = postBookmarkService.isBookmarked(postId, email);
        Map<String, Boolean> response = Map.of("isBookmarked", isBookmarked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/me/posts/bookmarks")
    @Operation(summary = "북마크한 게시글 목록 조회", description = "사용자가 북마크한 게시글 목록을 조회합니다.")
    public ResponseEntity<?> getBookmarkedPosts(
            Pageable pageable){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        PostBookmarkResponseDTO bookmarkedPosts = postBookmarkService.getBookmarkedPosts(email, pageable);
        return ResponseEntity.ok(bookmarkedPosts);
    }
}
