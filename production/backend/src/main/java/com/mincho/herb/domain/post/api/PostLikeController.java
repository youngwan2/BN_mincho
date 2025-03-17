package com.mincho.herb.domain.post.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.post.application.postLike.PostLikeService;
import com.mincho.herb.domain.post.dto.ResponseLikeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final CommonUtils commonUtils;


    @PostMapping("/{id}/like")
    public ResponseEntity<?> addPostLike(@PathVariable("id") Long id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        boolean state = postLikeService.addPostLike(id, email);
        ResponseLikeDTO responseLikeDTO = new ResponseLikeDTO(state);
        return new SuccessResponse<>().getResponse(200, "성공적으로 반영되었습니다.", HttpSuccessType.OK, responseLikeDTO);

    }
}
