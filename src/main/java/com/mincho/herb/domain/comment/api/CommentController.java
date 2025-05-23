package com.mincho.herb.domain.comment.api;

import com.mincho.herb.domain.comment.application.CommentService;
import com.mincho.herb.domain.comment.dto.CommentCreateRequestDTO;
import com.mincho.herb.domain.comment.dto.CommentResponseDTO;
import com.mincho.herb.domain.comment.dto.CommentUpdateRequestDTO;
import com.mincho.herb.domain.comment.dto.MypageCommentsDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class CommentController {
    private final CommonUtils commonUtils;
    private final CommentService commentService;

    // 댓글 추가
    @PostMapping("/community/posts/{postId}/comments/{commentId}")
    public ResponseEntity<?> addComment(@PathVariable @NotNull(message ="postId 는 필수입니다.") Long postId,
                                        @PathVariable @Nullable Long commentId,
                                        @Valid @RequestBody CommentCreateRequestDTO commentCreateRequestDTO,
                                        BindingResult result){
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        // 경로 파라미터에서 읽은 포스트와 코멘트 id 를 설정
        commentCreateRequestDTO.setParentCommentId(commentId);
        commentCreateRequestDTO.setPostId(postId);

        commentService.addComment(commentCreateRequestDTO, email);
        return new SuccessResponse<>().getResponse(201, "성공적으로 댓글이 추가되었습니다.", HttpSuccessType.CREATED);
    }

    // 댓글 조회
    @GetMapping("/community/posts/{postId}/comments")
    public ResponseEntity<?> getComments(@PathVariable Long postId){
        log.info("postId: {}", postId);
        CommentResponseDTO comment = commentService.getCommentsByPostId(postId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 댓글을 조회하였습니다.", HttpSuccessType.OK, comment );
    }

    // 댓글 수정
    @PatchMapping("/community/comments/{commentId}")
    public ResponseEntity<?> patchComment(
            @PathVariable @Nullable Long commentId,
            @RequestBody CommentUpdateRequestDTO commentUpdateRequestDTO
            ){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

            commentUpdateRequestDTO.setId(commentId);
            commentService.updateComment(commentUpdateRequestDTO);

        return new SuccessResponse<>().getResponse(200, "성공적으로 처리되었습니다.", HttpSuccessType.OK);
    }

    // 댓글 삭제
    @DeleteMapping("/community/comments/{commentId}")
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

    /** 마이페이지 */
    // 유저별 댓글 조회
    @GetMapping("/users/me/comments")
    public ResponseEntity<List<MypageCommentsDTO>> getMypageComments(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false, defaultValue = "desc") String sort
    ){
        return ResponseEntity.ok(commentService.getMypageComments(page,size,sort ));
    }
}
