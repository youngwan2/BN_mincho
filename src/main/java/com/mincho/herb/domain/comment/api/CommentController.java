package com.mincho.herb.domain.comment.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.comment.application.CommentService;
import com.mincho.herb.domain.comment.dto.RequestCommentCreateDTO;
import com.mincho.herb.domain.comment.dto.RequestCommentUpdateDTO;
import com.mincho.herb.domain.comment.dto.ResponseCommentDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class CommentController {

    private final CommonUtils commonUtils;
    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> addComment(@PathVariable @NotNull(message ="postId 는 필수입니다.") Long postId,
                                        @PathVariable @Nullable Long commentId,
                                        @Valid @RequestBody RequestCommentCreateDTO requestCommentCreateDTO,
                                        BindingResult result){
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        // 경로 파라미터에서 읽은 포스트와 코멘트 id 를 설정
        requestCommentCreateDTO.setParentCommentId(commentId);
        requestCommentCreateDTO.setPostId(postId);

        commentService.addComment(requestCommentCreateDTO, email);
        return new SuccessResponse<>().getResponse(201, "성공적으로 댓글이 추가되었습니다.", HttpSuccessType.CREATED);
    }

    // 댓글 조회
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId){
        log.info("postId: {}", postId);
        ResponseCommentDTO comment = commentService.getCommentsByPostId(postId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 댓글을 조회하였습니다.", HttpSuccessType.OK, comment );
    }

    // 댓글 수정
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<?> patchComment(
            @PathVariable @Nullable Long commentId,
            @RequestBody RequestCommentUpdateDTO requestCommentUpdateDTO
            ){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

            requestCommentUpdateDTO.setId(commentId);
            commentService.updateComment(requestCommentUpdateDTO);

        return new SuccessResponse<>().getResponse(200, "성공적으로 처리되었습니다.", HttpSuccessType.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @PathVariable @Nullable Long commentId
    ){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }

}
