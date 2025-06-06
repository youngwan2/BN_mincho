package com.mincho.herb.domain.post.api;


import com.mincho.herb.domain.post.application.postCategory.PostCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
public class PostCategoryController {
    private  final PostCategoryService postCategoryService;

    @GetMapping("/categories")
    @Operation(summary = "게시글 카테고리 조회", description = "게시글 카테고리를 조회합니다.")
    public ResponseEntity<?> getPostCategories() {
        return ResponseEntity.ok(postCategoryService.getPostCategories());
    }
}
