package com.mincho.herb.domain.post.api;

import com.mincho.herb.domain.post.application.postStatistics.PostStatisticsService;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.global.config.success.HttpSuccessType;
import com.mincho.herb.global.config.success.SuccessResponse;
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
public class PostStatisticsController {

    private final PostStatisticsService postStatisticsService;

    // 게시글 통계
    @GetMapping()
    public ResponseEntity<?> getPostStatistics(){

        List<PostCountDTO> postCountDTOs = postStatisticsService.getPostStatistics();

        log.info("post 통계: {}", postCountDTOs);

        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, postCountDTOs);

    }
}