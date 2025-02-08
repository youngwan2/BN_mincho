package com.mincho.herb.domain.comment.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.comment.application.CommentService;
import com.mincho.herb.domain.comment.dto.RequestCommentDTO;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts/{postId}/comments/{parentCommentId}")
public class CommentController {

    private final CommonUtils commonUtils;
    private final CommentService commentService;

    @PostMapping()
    public ResponseEntity<?> addComment(@PathVariable @NotNull(message ="postId 는 필수입니다.") Long postId,
                                        @PathVariable @Nullable Long parentCommentId,
                                        @Valid @RequestBody RequestCommentDTO requestCommentDTO,
                                        BindingResult result){
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"요청 권한이 없습니다.");
        }

        requestCommentDTO.setParentCommentId(parentCommentId);
        requestCommentDTO.setPostId(postId);

        commentService.addComment(requestCommentDTO, email);
        return new SuccessResponse<>().getResponse(201, "성공적으로 댓글이 추가되었습니다.", HttpSuccessType.CREATED);
    }
}
