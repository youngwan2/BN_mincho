package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.postLike.PostLikeService;
import com.mincho.herb.domain.post.dto.LikeResponseDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.global.util.CommonUtils;
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
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final CommonUtils commonUtils;

    // 좋아요 추가
    @PostMapping("/{id}/likes")
    public ResponseEntity<?> addPostLike(@PathVariable("id") Long id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        boolean state = postLikeService.addPostLike(id, email);

        LikeResponseDTO likeResponseDTO = new LikeResponseDTO(state);
        return new SuccessResponse<>().getResponse(200, "성공적으로 반영되었습니다.", HttpSuccessType.OK, likeResponseDTO);
    }
}
