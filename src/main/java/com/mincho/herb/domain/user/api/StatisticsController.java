package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.statistics.StatisticsService;
import com.mincho.herb.domain.user.dto.StatisticsResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(summary = "사용자 콘텐츠 통계", description = "내 콘텐츠 통계 조회 API")
    @GetMapping("/users/me/stats")
    public ResponseEntity<StatisticsResponseDTO> getStats(){
        StatisticsResponseDTO statisticsResponseDTO = statisticsService.getStat();

        return ResponseEntity.ok(statisticsResponseDTO);
    }
}
