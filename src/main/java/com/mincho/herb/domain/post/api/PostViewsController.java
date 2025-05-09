package com.mincho.herb.domain.post.api;


import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.domain.post.application.postView.PostViewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PostViewsController {

    private final PostViewsService postViewsService;

    // 포스트 조회수 증가
    @PatchMapping("/community/posts/{id}/view-count")
    public ResponseEntity<?> updatePostViewCount(
            @PathVariable() Long id
    ){

        if(id == null){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "포스트 식별을 위한 id 값은 필수입니다.");
        }

        postViewsService.updateViewCount(id);

        return ResponseEntity.noContent().build();
    }
}
