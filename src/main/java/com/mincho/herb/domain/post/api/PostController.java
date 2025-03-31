package com.mincho.herb.domain.post.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.dto.PostRequestDTO;
import com.mincho.herb.domain.post.dto.DetailPostResponseDTO;
import com.mincho.herb.domain.post.dto.PostResponseDTO;
import com.mincho.herb.domain.post.dto.SearchConditionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
@Slf4j
public class PostController {

    private final CommonUtils commonUtils;
    private final PostService postService;

    // 게시글 조회
    @GetMapping()
    @Valid
    public ResponseEntity<?> getPostsByCategory(
            @RequestParam("category") @NotEmpty(message= "category 는 필수입니다.") String category,
            @RequestParam("query") String query,
            @RequestParam("sort") @NotEmpty(message= "sort 는 필수입니다.") String sort,
            @RequestParam("order") @NotEmpty(message="order 는 필수입니다.") String order,
            @RequestParam("page") @Min(value = 0, message = "page 는 최소 0 이상이어야 합니다.") Integer page,
            @RequestParam("size") @Min(value = 5, message = "size 는 최소 5 이상이어야 합니다.") Integer size
            ){

        log.info("category {} query {} sort {}  order{} page{} size{}", category, query, sort, order, page, size);

        SearchConditionDTO searchCondition = SearchConditionDTO.builder()
                .order(order) // asc, desc
                .query(query)
                .sort(sort) // 정렬 기준 post_id, like_count 등등
                .category(category)
                .build();

        PostResponseDTO posts = postService.getPostsByCondition(page, size, searchCondition);
        return new SuccessResponse<>().getResponse(200, "조회되었습니다.", HttpSuccessType.OK, posts);

    }

    // 게시글 추가
    @PostMapping()
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
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailPost(@PathVariable("id") Long id){
        if(id == null){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }

        DetailPostResponseDTO detailPostResponseDTO =  postService.getDetailPostById(id);

        return new SuccessResponse<>().getResponse(200, "정상적으로 조회되었습니다.", HttpSuccessType.OK, detailPostResponseDTO);

    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
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
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable("id") Long id,
            @Valid @RequestBody PostRequestDTO postRequestDTO,
            BindingResult result) {
        if (id == null) {
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }

        if (result.hasErrors()) {
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!commonUtils.emailValidation(email)) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        postService.update(postRequestDTO, id, email);

        return new SuccessResponse<>().getResponse(200, "성공적으로 수정되었습니다.", HttpSuccessType.OK);
    }

}
