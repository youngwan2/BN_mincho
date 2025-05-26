package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.postLike.PostLikeService;
import com.mincho.herb.domain.post.dto.LikeResponseDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.global.util.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
@Tag(name = "Post Like", description = "게시글 좋아요 관련 API")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final AuthUtils authUtils;

    // 좋아요 추가
    @PostMapping("/{id}/likes")
    @Operation(summary = "게시글 좋아요 추가/토글", description = "게시글에 좋아요를 추가하거나 토글합니다.")
    public ResponseEntity<?> addPostLike(
            @Parameter(description = "게시글 ID", required = true) @PathVariable("id") Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!authUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        boolean state = postLikeService.addPostLike(id, email);

        LikeResponseDTO likeResponseDTO = new LikeResponseDTO(state);
        return new SuccessResponse<>().getResponse(200, "성공적으로 반영되었습니다.", HttpSuccessType.OK, likeResponseDTO);
    }
}
