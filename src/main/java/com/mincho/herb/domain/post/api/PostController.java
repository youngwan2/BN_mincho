package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.infra.auth.S3Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

    /** 게시글 목록 조회 */
    @GetMapping("/community/posts")
    public ResponseEntity<?> getPostsByCategory(
            @RequestParam("category") @NotBlank(message = "category는 필수입니다.") String category,
            @RequestParam(value = "queryType", defaultValue = "content") String queryType,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "sort", defaultValue = "desc") @NotBlank(message = "sort는 필수입니다.") String sort,
            @RequestParam(value = "order", defaultValue = "post_id") @NotBlank(message = "order는 필수입니다.") String order,
            @RequestParam("page") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @RequestParam("size") @Min(value = 5, message = "size는 최소 5 이상이어야 합니다.") int size
    ) {

        SearchConditionDTO searchCondition = SearchConditionDTO.builder()
                .category(category)
                .queryType(queryType)
                .query(query)
                .sort(sort)
                .order(order)
                .build();

        PostResponseDTO posts = postService.getPostsByCondition(page, size, searchCondition);
        return new SuccessResponse<>().getResponse(200, "조회되었습니다.", HttpSuccessType.OK, posts);
    }

    /** 게시글 등록 */
    @PostMapping("/community/posts")
    public ResponseEntity<Map<String, String>> addPost(@Valid @RequestBody PostRequestDTO postRequestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.addPost(postRequestDTO, email);
        return new SuccessResponse<>().getResponse(201, "추가되었습니다.", HttpSuccessType.CREATED);
    }

    /** 게시글 상세 조회 */
    @GetMapping("/community/posts/{id}")
    public ResponseEntity<?> getDetailPost(@PathVariable Long id) {
        DetailPostResponseDTO detailPost = postService.getDetailPostById(id);
        return new SuccessResponse<>().getResponse(200, "정상적으로 조회되었습니다.", HttpSuccessType.OK, detailPost);
    }

    /** 게시글 삭제 */
    @DeleteMapping("/community/posts/{id}")
    public ResponseEntity<Map<String, String>> removePost(@PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.removePost(id, email);
        return new SuccessResponse<>().getResponse(200, "정상적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }

    /** 게시글 수정 */
    @PatchMapping("/community/posts/{id}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDTO postRequestDTO
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.update(postRequestDTO, id, email);
        return new SuccessResponse<>().getResponse(200, "성공적으로 수정되었습니다.", HttpSuccessType.OK);
    }

    /** 이미지 업로드용 프리사인드 URL */
    @PostMapping("/community/posts/images/presigned-url")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = s3Service.generatePresignedUrl(file, 5);
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("url", url);
        return ResponseEntity.ok(urlMap);
    }

    /** 마이페이지 - 사용자 게시글 조회 */
    @GetMapping("/users/me/posts")
    public ResponseEntity<List<MypagePostsDTO>> getMypagePosts(
            @RequestParam("page") @Min(0) int page,
            @RequestParam("size") @Min(1) int size
    ) {
        List<MypagePostsDTO> posts = postService.getUserPosts(page, size);
        return ResponseEntity.ok(posts);
    }
}
