package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 배너 클릭/노출 등 이벤트성 API 전용 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banner Event", description = "배너 클릭/노출 이벤트 API")
public class BannerEventController {
    private final BannerEventService bannerEventService;

    @PostMapping("/{id}/click")
    @Operation(summary = "배너 클릭 처리", description = "배너 클릭 이벤트를 처리합니다.")
    public ResponseEntity<Void> handleBannerClick(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id) {
        bannerEventService.handleBannerClick(id);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "배너 노출 처리", description = "배너 노출 이벤트를 처리합니다.")
    public ResponseEntity<Void> handleBannerView(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id) {
        bannerEventService.handleBannerView(id);
        return ResponseEntity.status(201).build();
    }
}

