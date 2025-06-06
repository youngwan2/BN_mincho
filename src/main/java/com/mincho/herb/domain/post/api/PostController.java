package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import com.mincho.herb.infra.auth.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
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
@Tag(name = "Post", description = "커뮤니티 게시글 관련 API")
public class PostController {

    private final PostService postService;
    private final S3Service s3Service;

    /** 게시글 목록 조회 */
    @GetMapping("/community/posts")
    @Operation(summary = "게시글 목록 조회", description = "카테고리 및 조건에 따라 게시글 목록을 조회합니다.")
    public ResponseEntity<?> getPostsByCategory(
            @Parameter(description = "카테고리", required = true) @RequestParam("categoryId") Long categoryId,
            @Parameter(description = "검색 타입", required = false) @RequestParam(value = "queryType", defaultValue = "content") String queryType,
            @Parameter(description = "검색어", required = false) @RequestParam(value = "query", required = false) String query,
            @Parameter(description = "정렬 기준", required = false) @RequestParam(value = "sort", defaultValue = "post_id") @NotBlank(message = "sort는 필수입니다.") String sort,
            @Parameter(description = "정렬 방향", required = false) @RequestParam(value = "order", defaultValue = "desc") @NotBlank(message = "order는 필수입니다.") String order,
            @Parameter(description = "페이지 번호", required = true) @RequestParam("page") @Min(value = 0, message = "page는 0 이상이어야 합니다.") int page,
            @Parameter(description = "페이지 크기", required = true) @RequestParam("size") @Min(value = 5, message = "size는 최소 5 이상이어야 합니다.") int size
    ) {

        SearchConditionDTO searchCondition = SearchConditionDTO.builder()
                .categoryId(categoryId)
                .queryType(queryType)
                .query(query)
                .sort(sort)
                .order(order)
                .build();

        PostResponseDTO response = postService.getPostsByCondition(page, size, searchCondition);
        return ResponseEntity.ok(response);
    }


    /** 게시글 상세 조회 */
    @GetMapping("/community/posts/{id}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getDetailPost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        DetailPostResponseDTO detailPost = postService.getDetailPostById(id);
        return new SuccessResponse<>().getResponse(200, "정상적으로 조회되었습니다.", HttpSuccessType.OK, detailPost);
    }

    /** 게시글 삭제 */
    @DeleteMapping("/community/posts/{id}")
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    public ResponseEntity<Map<String, String>> removePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.removePost(id, email);
        return new SuccessResponse<>().getResponse(200, "정상적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }

    /** 게시글 등록 */
    @PostMapping("/community/posts")
    @Operation(summary = "게시글 등록", description = "새로운 게시글을 등록합니다.")
    public ResponseEntity<Map<String, String>> addPost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시글 등록 요청 DTO", required = true)
            @Valid @RequestBody PostRequestDTO postRequestDTO) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.addPost(postRequestDTO, email);
        return new SuccessResponse<>().getResponse(201, "추가되었습니다.", HttpSuccessType.CREATED);
    }


    /** 게시글 수정 */
    @PatchMapping("/community/posts/{id}")
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    public ResponseEntity<Map<String, String>> updatePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시글 수정 요청 DTO", required = true)
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
    @Operation(summary = "이미지 업로드 presigned URL 생성", description = "S3에 이미지 업로드를 위한 presigned URL을 생성합니다.")
    public ResponseEntity<Map<String, String>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true) @RequestParam("file") MultipartFile file) {
        String url = s3Service.generatePresignedUrl(file, 5);
        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("url", url);
        return ResponseEntity.ok(urlMap);
    }

    @GetMapping("/users/{userId}/posts")
    @Operation(summary = "사용자 게시글 조회", description = "특정 사용자의 게시글 목록을 조회합니다.")
    public ResponseEntity<UserPostResponseDTO> getUserPosts(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId,
            Pageable pageable
    ) {
        UserPostResponseDTO userPostResponseDTO= postService.getUserPostsByUserId(userId, pageable);
        return ResponseEntity.ok(userPostResponseDTO);
    }

    /** 마이페이지 - 사용자 게시글 조회 */
    @GetMapping("/users/me/posts")
    @Operation(summary = "마이페이지 게시글 조회", description = "마이페이지에서 사용자의 게시글 목록을 조회합니다.")
    public ResponseEntity<List<MypagePostsDTO>> getMypagePosts(
            @Parameter(description = "페이지 번호", required = true) @RequestParam("page") @Min(0) int page,
            @Parameter(description = "페이지 크기", required = true) @RequestParam("size") @Min(1) int size
    ) {
        List<MypagePostsDTO> posts = postService.getUserPosts(page, size);
        return ResponseEntity.ok(posts);
    }
}
