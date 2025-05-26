package com.mincho.herb.domain.comment.api;

import com.mincho.herb.domain.comment.application.CommentService;
import com.mincho.herb.domain.comment.dto.CommentCreateRequestDTO;
import com.mincho.herb.domain.comment.dto.CommentResponseDTO;
import com.mincho.herb.domain.comment.dto.CommentUpdateRequestDTO;
import com.mincho.herb.domain.comment.dto.MypageCommentsDTO;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {

    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/community/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> addComment(
            @PathVariable Long postId,
            @PathVariable(required = false) Long commentId,
            @Valid @RequestBody CommentCreateRequestDTO commentCreateRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentCreateRequestDTO.setPostId(postId);
        commentCreateRequestDTO.setParentCommentId(commentId);

        commentService.addComment(commentCreateRequestDTO, email);
        return new SuccessResponse<>().getResponse(201, "성공적으로 댓글이 추가되었습니다.", HttpSuccessType.CREATED);
    }

    // 댓글 조회
    @GetMapping("/community/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        log.info("postId: {}", postId);
        CommentResponseDTO comment = commentService.getCommentsByPostId(postId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 댓글을 조회하였습니다.", HttpSuccessType.OK, comment);
    }

    // 댓글 수정
    @PatchMapping("/community/comments/{commentId}")
    public ResponseEntity<?> patchComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequestDTO commentUpdateRequestDTO) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentUpdateRequestDTO.setId(commentId);
        commentService.updateComment(commentUpdateRequestDTO);

        return new SuccessResponse<>().getResponse(200, "성공적으로 처리되었습니다.", HttpSuccessType.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/community/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 없습니다.");
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // 마이페이지 - 유저별 댓글 조회
    @GetMapping("/users/me/comments")
    public ResponseEntity<List<MypageCommentsDTO>> getMypageComments(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "desc") String sort) {
        return ResponseEntity.ok(commentService.getMypageComments(page, size, sort));
    }
}
