package com.mincho.herb.domain.post.api;

import com.mincho.herb.global.config.error.ErrorResponse;
import com.mincho.herb.global.config.error.HttpErrorType;
import com.mincho.herb.global.config.success.HttpSuccessType;
import com.mincho.herb.global.config.success.SuccessResponse;
import com.mincho.herb.global.util.CommonUtils;
import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.infra.auth.S3Service;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
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

    private final CommonUtils commonUtils;
    private final PostService postService;
    private final S3Service s3Service;

    // 게시글 조회
    @GetMapping("/community/posts")
    @Valid
    public ResponseEntity<?> getPostsByCategory(
            @RequestParam("category") @NotEmpty(message= "category 는 필수입니다.") String category,
            @RequestParam(value = "queryType", defaultValue = "content", required = false) String queryType,
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "sort",defaultValue = "desc") @NotEmpty(message= "sort 는 필수입니다.") String sort,
            @RequestParam(value ="order",defaultValue = "post_id" ) @NotEmpty(message="order 는 필수입니다.") String order,
            @RequestParam("page") @Min(value = 0, message = "page 는 최소 0 이상이어야 합니다.") Integer page,
            @RequestParam("size") @Min(value = 5, message = "size 는 최소 5 이상이어야 합니다.") Integer size
            ){

        log.info("category {} query {} sort {}  order{} page{} size{}", category, query, sort, order, page, size);

        SearchConditionDTO searchCondition = SearchConditionDTO.builder()
                .order(order) // 정렬 기준 post_id, like_count 등등
                .query(query) // 검색어
                .queryType(queryType) // 검색 대상 title 혹은 content
                .sort(sort) //  정렬 asc, desc
                .category(category) 
                .build();

        PostResponseDTO posts = postService.getPostsByCondition(page, size, searchCondition);
        return new SuccessResponse<>().getResponse(200, "조회되었습니다.", HttpSuccessType.OK, posts);

    }

    // 게시글 추가
    @PostMapping("/community/posts")
    public ResponseEntity<Map<String,String>> addPost(@RequestBody PostRequestDTO postRequestDTO, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        postService.addPost(postRequestDTO, email);

        return new SuccessResponse<>().getResponse(201, "추가 되었습니다.", HttpSuccessType.CREATED);
    }

    // 게시글 상세 조회
    @GetMapping("/community/posts/{id}")
    public ResponseEntity<?> getDetailPost(
            @PathVariable("id") Long id ) {


        if(id == null){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }

        DetailPostResponseDTO detailPostResponseDTO =  postService.getDetailPostById(id);

        return new SuccessResponse<>().getResponse(200, "정상적으로 조회되었습니다.", HttpSuccessType.OK, detailPostResponseDTO);

    }

    // 게시글 삭제
    @DeleteMapping("/community/posts/{id}")
    public ResponseEntity<Map<String, String>> removePost(@PathVariable("id") Long id){
        if(id == null){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.removePost(id, email);

        return new SuccessResponse<>().getResponse(200, "정상적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }

    // 게시글 수정
    @PatchMapping("/community/posts/{id}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostRequestDTO postRequestDTO
        ) {
        if (id == null) {
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }



        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!commonUtils.emailValidation(email)) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.update(postRequestDTO, id, email);

        return new SuccessResponse<>().getResponse(200, "성공적으로 수정되었습니다.", HttpSuccessType.OK);
    }

    // 게시글 이미지 프리사인드 URL 생성
    @PostMapping("/community/posts/images/presigned-url")
    public ResponseEntity<?> uploadImage(
            @RequestParam("file") MultipartFile file
    ){
        String url  =s3Service.generatePresignedUrl(file, 5);

        Map<String, String> urlMap = new HashMap<String, String>();
        urlMap.put("url", url);
        return ResponseEntity.ok(urlMap);
    }


    /** 마이페이지*/
    // 사용자가 작성한 게시글 목록
    @GetMapping("/users/me/posts")
    public ResponseEntity<List<MypagePostsDTO>> getMypagePosts(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ){
        List<MypagePostsDTO> posts = postService.getUserPosts(page, size);
        return ResponseEntity.ok(posts);
    }
}
