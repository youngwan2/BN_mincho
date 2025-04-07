package com.mincho.herb.domain.user.api;

import com.mincho.herb.domain.user.application.statistics.StatisticsService;
import com.mincho.herb.domain.user.dto.StatisticsResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class StatisticsController {

    private  final StatisticsService statisticsService;

    // 사용자 콘텐츠 통계
    @GetMapping("/users/me/stats")
    public ResponseEntity<StatisticsResponseDTO> getStats(){
        StatisticsResponseDTO statisticsResponseDTO = statisticsService.getStat();

        return ResponseEntity.ok(statisticsResponseDTO);
    }
}
