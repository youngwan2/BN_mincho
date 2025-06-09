package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.postView.PostViewsService;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
@Tag(name = "Post Views", description = "게시글 조회수 관련 API")
public class PostViewsController {

    private final PostViewsService postViewsService;

    // 포스트 조회수 증가
    @PatchMapping("/{id}/view")
    @Operation(summary = "게시글 조회수 증가", description = "게시글의 조회수를 1 증가시킵니다.")
    public ResponseEntity<?> updatePostViewCount(
            @Parameter(description = "게시글 ID", required = true) @PathVariable() Long id
    ) {

        if(id == null){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "포스트 식별을 위한 id 값은 필수입니다.");
        }

        postViewsService.updateViewCount(id);

        return ResponseEntity.noContent().build();
    }

    // 게시글 조회
    @GetMapping("/{id}/view")
    @Operation(summary = "게시글 조회수 조회", description = "게시글의 조회수를 조회합니다.")
    public ResponseEntity<Long> getPostViewCount(
            @Parameter(description = "게시글 ID", required = true) @PathVariable() Long id
    ) {

        if(id == null){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "포스트 식별을 위한 id 값은 필수입니다.");
        }

        Long viewCount = postViewsService.getPostViewCount(id);

        return ResponseEntity.ok(viewCount);
    }
}
