package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerStatisticsService;
import com.mincho.herb.domain.banner.dto.BannerStatisticsResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/banners/statistics")
@RequiredArgsConstructor
@Tag(name = "Banner Statistics", description = "배너 통계 API")
public class BannerStatisticsController {

    private final BannerStatisticsService bannerStatisticsService;

    @GetMapping
    @Operation(summary = "배너 통계 조회", description = "전체 배너 통계를 조회합니다.")
    public ResponseEntity<BannerStatisticsResponseDTO> getBannerStatistics() {
        BannerStatisticsResponseDTO response = bannerStatisticsService.getBannerStatistics();
        return ResponseEntity.ok(response);
    }
}
