package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.statistics.StatisticsService;
import com.mincho.herb.domain.user.dto.StatisticsResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "통계", description = "사용자 통계 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class StatisticsController {

    private  final StatisticsService statisticsService;

    /** 사용자 콘텐츠 통계 */
    @Operation(
        summary = "사용자 콘텐츠 통계",
        description = "내 작성한 콘텐츠(질문, 답변, 게시글) 통계 조회 API"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "통계 조회 성공"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping("/users/me/stats")
    public ResponseEntity<StatisticsResponseDTO> getStats(){
        StatisticsResponseDTO statisticsResponseDTO = statisticsService.getStat();

        return ResponseEntity.ok(statisticsResponseDTO);
    }
}
