package com.mincho.herb.domain.comment.api;

import com.mincho.herb.domain.comment.application.CommentService;
import com.mincho.herb.domain.comment.dto.CommentCreateRequestDTO;
import com.mincho.herb.domain.comment.dto.CommentResponseDTO;
import com.mincho.herb.domain.comment.dto.CommentUpdateRequestDTO;
import com.mincho.herb.domain.comment.dto.MypageCommentsDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Comment", description = "댓글 관련 API")
public class CommentController {

    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/community/posts/{postId}/comments/{commentId}")
    @Operation(summary = "댓글 추가", description = "게시글에 댓글을 추가합니다.")
    public ResponseEntity<Void> addComment(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "부모 댓글 ID(대댓글)", required = false) @PathVariable(required = false) Long commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 생성 요청 DTO", required = true)
            @Valid @RequestBody CommentCreateRequestDTO commentCreateRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentCreateRequestDTO.setPostId(postId);
        commentCreateRequestDTO.setParentCommentId(commentId);

        commentService.addComment(commentCreateRequestDTO, email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 댓글 조회
    @GetMapping("/community/posts/{postId}/comments")
    @Operation(summary = "댓글 조회", description = "게시글의 댓글 목록을 조회합니다.")
    public ResponseEntity<CommentResponseDTO> getComments(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        log.info("postId: {}", postId);
        CommentResponseDTO comment = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PatchMapping("/community/comments/{commentId}")
    @Operation(summary = "댓글 수정", description = "댓글을 수정합니다.")
    public ResponseEntity<Void> patchComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 수정 요청 DTO", required = true)
            @Valid @RequestBody CommentUpdateRequestDTO commentUpdateRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentUpdateRequestDTO.setId(commentId);
        commentService.updateComment(commentUpdateRequestDTO);

        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/community/comments/{commentId}")
    @Operation(summary = "댓글 삭제", description = "댓글을 삭제합니다.")
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 마이페이지 - 유저별 댓글 조회
    @GetMapping("/users/me/comments")
    @Operation(summary = "마이페이지 댓글 조회", description = "마이페이지에서 사용자의 댓글 목록을 조회합니다.")
    public ResponseEntity<List<MypageCommentsDTO>> getMypageComments(
            @Parameter(description = "페이지 번호", required = true) @RequestParam int page,
            @Parameter(description = "페이지 크기", required = true) @RequestParam int size,
            @Parameter(description = "정렬 방식", required = false) @RequestParam(required = false, defaultValue = "desc") String sort) {
        return ResponseEntity.ok(commentService.getMypageComments(page, size, sort));
    }
}
