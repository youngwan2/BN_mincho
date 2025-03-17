package com.mincho.herb.domain.like.api;


import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.domain.like.application.HerbLikeService;
import com.mincho.herb.domain.like.dto.HerbLikeCountResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class HerbLikeController {

    private final HerbLikeService herbLikeService;

    // 좋아요 조회(카운트)
    @GetMapping("/herbs/{herbId}/likes")
    public ResponseEntity<?> getCountHerbLike(@PathVariable Long herbId){
       int count =  herbLikeService.countByHerbId(herbId);
       Boolean isHerbLiked=herbLikeService.isHerbLiked(herbId);

        HerbLikeCountResponseDTO herbLikeCountResponseDTO = HerbLikeCountResponseDTO.builder()
                .count(count)
                .isHerbLiked(isHerbLiked)
                .build();

        return ResponseEntity.ok(herbLikeCountResponseDTO);
    }

    // 좋아요 추가
    @PostMapping("/users/me/herbs/{herbId}/likes")
    public ResponseEntity<?> addHerbLike(@PathVariable Long herbId){
        log.info("log:{}","like add");
        herbLikeService.addHerbLike(herbId);
        return new SuccessResponse<>().getResponse(201, "성공적으로 좋아요를 추가하였습니다.", HttpSuccessType.CREATED);
    }

    // 좋아요 취소
    @DeleteMapping("/users/me/herbs/{herbId}/likes")
    public ResponseEntity<?> deleteHerbLike(@PathVariable Long herbId){
        log.info("log:{}","like del");
        herbLikeService.deleteHerbLike(herbId);
        return new SuccessResponse<>().getResponse(200, "성공적으로 좋아요를 취소하였습니다.", HttpSuccessType.CREATED);
    }
}
