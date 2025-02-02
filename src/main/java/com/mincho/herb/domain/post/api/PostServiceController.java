package com.mincho.herb.domain.post.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.util.CommonUtils;
import com.mincho.herb.domain.post.application.post.PostService;
import com.mincho.herb.domain.post.dto.RequestPostDTO;
import com.mincho.herb.domain.post.dto.ResponsePostDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class PostServiceController {

    private final CommonUtils commonUtils;
    private final PostService postService;

    @GetMapping()
    @Valid
    public ResponseEntity<?> getPostsByCategory(
            @RequestParam("category") @NotEmpty(message = "category 는 필수입니다.") String category,
            @RequestParam("page") @Min(value = 0, message = "page 는 최소 0 이상이어야 합니다.") Integer page,
            @RequestParam("size") @Min(value = 5, message = "size 는 최소 5 이상이어야 합니다.") Integer size
            ){

        List<ResponsePostDTO> posts = postService.getPostsByCategory(page, size, category);
        return new SuccessResponse<>().getResponse(200, "조회되었습니다.", HttpSuccessType.OK, posts);

    }

    @PostMapping()
    public ResponseEntity<Map<String,String>> addPost(@RequestBody RequestPostDTO requestPostDTO, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!commonUtils.emailValidation(email)){
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        postService.addPost(requestPostDTO, email);

        return new SuccessResponse<>().getResponse(201, "추가 되었습니다.", HttpSuccessType.CREATED);
    }

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

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> updatePost(
            @PathVariable("id") Long id,
            @RequestBody RequestPostDTO requestPostDTO){
        if(id == null){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. 경로 파라미터를 재확인 해주세요.", HttpErrorType.BAD_REQUEST);
        }

        return null;
    }
}
