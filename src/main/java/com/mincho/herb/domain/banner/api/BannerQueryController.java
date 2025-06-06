package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerQueryService;
import com.mincho.herb.domain.banner.dto.BannerListResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 배너 단건/목록/활성/만료 등 조회 전용 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/admin/banners")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Banner Query", description = "배너 조회 API")
public class BannerQueryController {
    private final BannerQueryService bannerQueryService;

    @GetMapping("/{id}")
    @Operation(summary = "배너 조회", description = "특정 배너를 조회합니다.")
    public ResponseEntity<BannerResponseDTO> getBanner(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 ID", required = true) @PathVariable Long id) {
        BannerResponseDTO response = bannerQueryService.getBanner(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @Operation(summary = "배너 목록 조회", description = "조건에 따라 배너를 검색합니다.")
    public ResponseEntity<BannerListResponseDTO> getBanners(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 제목") @RequestParam(required = false) String title,
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 카테고리") @RequestParam(required = false) String category,
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 상태") @RequestParam(required = false) BannerStatusEnum status,
            @io.swagger.v3.oas.annotations.Parameter(description = "시작일") @RequestParam(required = false) LocalDateTime startDate,
            @io.swagger.v3.oas.annotations.Parameter(description = "종료일") @RequestParam(required = false) LocalDateTime endDate,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @io.swagger.v3.oas.annotations.Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size,
            @io.swagger.v3.oas.annotations.Parameter(description = "정렬 기준") @RequestParam(defaultValue = "createdAt") String sortBy,
            @io.swagger.v3.oas.annotations.Parameter(description = "정렬 방향") @RequestParam(defaultValue = "desc") String sortDir) {

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

        Page<BannerResponseDTO> bannerPage = bannerQueryService.getBanners(criteria, pageable);

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
        List<BannerResponseDTO> response = bannerQueryService.getActiveBanners();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active/{name}")
    @Operation(summary = "카테고리별 활성 배너 조회", description = "특정 카테고리의 활성화된 배너를 조회합니다.")
    public ResponseEntity<List<BannerResponseDTO>> getActiveBannersByCategory(
            @io.swagger.v3.oas.annotations.Parameter(description = "배너 카테고리", required = true) @PathVariable String category) {
        List<BannerResponseDTO> response = bannerQueryService.getActiveBannersByCategory(category);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring")
    @Operation(summary = "만료 예정 배너 조회", description = "지정된 일수 내에 만료되는 배너를 조회합니다.")
    public ResponseEntity<List<BannerResponseDTO>> getBannersExpiringWithinDays(
            @io.swagger.v3.oas.annotations.Parameter(description = "만료까지 남은 일수", required = false) @RequestParam(defaultValue = "7") int days) {
        List<BannerResponseDTO> response = bannerQueryService.getBannersExpiringWithinDays(days);
        return ResponseEntity.ok(response);
    }
}

