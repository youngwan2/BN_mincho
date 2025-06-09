package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerCommandService;
import com.mincho.herb.domain.banner.dto.*;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 배너 생성/수정/삭제/상태/순서 변경 등 명령성 API 전용 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banner Command", description = "배너 생성/수정/삭제/상태/순서 변경 API")
public class BannerCommandController {
    private final BannerCommandService bannerCommandService;

    @PostMapping
    @Operation(summary = "배너 생성", description = "새로운 배너를 생성합니다.")
    public ResponseEntity<BannerResponseDTO> createBanner(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "배너 생성 요청 DTO", required = true)
            @Valid @RequestBody BannerCreateRequestDTO request) {
        log.info("배너 생성 요청: {}", request.getTitle());
        BannerResponseDTO response = bannerCommandService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "배너 수정", description = "기존 배너를 수정합니다.")
    public ResponseEntity<BannerResponseDTO> updateBanner(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "배너 수정 요청 DTO", required = true)
            @Valid @RequestBody BannerUpdateRequestDTO request) {
        log.info("배너 수정 요청: ID {}", id);
        BannerResponseDTO response = bannerCommandService.updateBanner(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다.")
    public ResponseEntity<Void> deleteBanner(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id) {

        log.info("배너 삭제 요청: ID {}", id);

        bannerCommandService.deleteBanner(id);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "배너 상태 변경", description = "배너의 상태를 변경합니다.")
    public ResponseEntity<BannerResponseDTO> changeBannerStatus(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "배너 상태 변경 요청 DTO", required = true)
            @Valid @RequestBody BannerStatusChangeRequestDTO request) {

        log.info("배너 상태 변경 요청: ID {}, 상태 {}", id, request.getStatus());

        BannerResponseDTO response = bannerCommandService.changeBannerStatus(id, BannerStatusEnum.valueOf(request.getStatus()));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/order")
    @Operation(summary = "배너 순서 변경", description = "배너의 표시 순서를 변경합니다.")
    public ResponseEntity<Void> updateBannerOrder(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "배너 순서 변경 요청 DTO", required = true)
            @Valid @RequestBody BannerOrderUpdateRequestDTO request) {

        log.info("배너 순서 변경 요청: ID {}, 순서 {}", id, request.getSortOrder());

        bannerCommandService.updateBannerOrder(id, request.getSortOrder());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update-expired")
    @Operation(summary = "만료된 배너 상태 업데이트", description = "만료된 배너들의 상태를 일괄 업데이트합니다.")
    public ResponseEntity<Void> updateExpiredBanners() {

        log.info("만료된 배너 상태 업데이트 요청");

        bannerCommandService.updateExpiredBanners();
        return ResponseEntity.ok().build();
    }
}

