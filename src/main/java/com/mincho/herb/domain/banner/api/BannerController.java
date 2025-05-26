package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerService;
import com.mincho.herb.domain.banner.dto.*;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banner Management", description = "배너 관리 API")
public class BannerController {

    private final BannerService bannerService;

    @PostMapping
    @Operation(summary = "배너 생성", description = "새로운 배너를 생성합니다.")
    public ResponseEntity<BannerResponseDTO> createBanner(@Valid @RequestBody BannerCreateRequestDTO request) {
        log.info("배너 생성 요청: {}", request.getTitle());
        BannerResponseDTO response = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "배너 수정", description = "기존 배너를 수정합니다.")
    public ResponseEntity<BannerResponseDTO> updateBanner(
            @PathVariable Long id,
            @Valid @RequestBody BannerUpdateRequestDTO request) {
        log.info("배너 수정 요청: ID {}", id);
        BannerResponseDTO response = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "배너 삭제", description = "배너를 삭제합니다.")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        log.info("배너 삭제 요청: ID {}", id);
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "배너 조회", description = "특정 배너를 조회합니다.")
    public ResponseEntity<BannerResponseDTO> getBanner(@PathVariable Long id) {
        BannerResponseDTO response = bannerService.getBanner(id);
        return ResponseEntity.ok(response);
    }


    @GetMapping()
    @Operation(summary = "배너 목록 조회", description = "조건에 따라 배너를 검색합니다.")
    public ResponseEntity<BannerListResponseDTO> getBanners(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BannerStatusEnum status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        BannerSearchCriteriaDTO criteria = BannerSearchCriteriaDTO.builder()
                .title(title)
                .category(category)
                .status(status)
                .startDate(startDate)
                .endDate(endDate)
                .build();

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BannerResponseDTO> bannerPage = bannerService.getBanners(criteria, pageable);

        BannerListResponseDTO response = BannerListResponseDTO.builder()
                .banners(bannerPage.getContent())
                .totalPages(bannerPage.getTotalPages())
                .totalElements(bannerPage.getTotalElements())
                .currentPage(bannerPage.getNumber())
                .pageSize(bannerPage.getSize())
                .hasNext(bannerPage.hasNext())
                .hasPrevious(bannerPage.hasPrevious())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    @Operation(summary = "활성 배너 조회", description = "현재 활성화된 모든 배너를 조회합니다.")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBanners() {
        List<BannerResponseDTO> response = bannerService.getActiveBanners();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{category}")
    @Operation(summary = "카테고리별 활성 배너 조회", description = "특정 카테고리의 활성화된 배너를 조회합니다.")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBannersByCategory(@PathVariable String category) {
        List<BannerResponseDTO> response = bannerService.getActiveBannersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "배너 상태 변경", description = "배너의 상태를 변경합니다.")
    public ResponseEntity<BannerResponseDTO> changeBannerStatus(
            @PathVariable Long id,
            @Valid @RequestBody BannerStatusChangeRequestDTO request) {
        log.info("배너 상태 변경 요청: ID {}, 상태 {}", id, request.getStatus());
        BannerResponseDTO response = bannerService.changeBannerStatus(id, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/order")
    @Operation(summary = "배너 순서 변경", description = "배너의 표시 순서를 변경합니다.")
    public ResponseEntity<Void> updateBannerOrder(
            @PathVariable Long id,
            @Valid @RequestBody BannerOrderUpdateRequestDTO request) {
        log.info("배너 순서 변경 요청: ID {}, 순서 {}", id, request.getSortOrder());
        bannerService.updateBannerOrder(id, request.getSortOrder());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/click")
    @Operation(summary = "배너 클릭 처리", description = "배너 클릭 이벤트를 처리합니다.")
    public ResponseEntity<Void> handleBannerClick(@PathVariable Long id) {
        bannerService.handleBannerClick(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "배너 노출 처리", description = "배너 노출 이벤트를 처리합니다.")
    public ResponseEntity<Void> handleBannerView(@PathVariable Long id) {
        bannerService.handleBannerView(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expiring")
    @Operation(summary = "만료 예정 배너 조회", description = "지정된 일수 내에 만료되는 배너를 조회합니다.")
    public ResponseEntity<List<BannerResponseDTO>> getBannersExpiringWithinDays(
            @RequestParam(defaultValue = "7") int days) {
        List<BannerResponseDTO> response = bannerService.getBannersExpiringWithinDays(days);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-expired")
    @Operation(summary = "만료된 배너 상태 업데이트", description = "만료된 배너들의 상태를 일괄 업데이트합니다.")
    public ResponseEntity<Void> updateExpiredBanners() {
        log.info("만료된 배너 상태 업데이트 요청");
        bannerService.updateExpiredBanners();
        return ResponseEntity.ok().build();
    }
}
