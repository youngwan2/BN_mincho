package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.postStatistics.PostStatisticsService;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/statistics/posts")
@Tag(name = "Post Statistics", description = "게시글 통계 API")
public class PostStatisticsController {

    private final PostStatisticsService postStatisticsService;

    @GetMapping()
    @Operation(summary = "게시글 통계 조회", description = "게시글 통계 정보를 조회합니다.")
    public ResponseEntity<?> getPostStatistics(){

        List<PostCountDTO> postCountDTOs = postStatisticsService.getPostStatistics();

        log.info("post 통계: {}", postCountDTOs);

        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, postCountDTOs);

    }
}
