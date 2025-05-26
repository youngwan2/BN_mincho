package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbAdminQueryService;
import com.mincho.herb.domain.herb.dto.HerbAdminResponseDTO;
import com.mincho.herb.domain.herb.dto.HerbFilteringConditionDTO;
import com.mincho.herb.domain.herb.dto.HerbSort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/herbs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
@Tag(name = "Herb Admin Query", description = "관리자 허브 조회 관련 API")
public class HerbAdminQueryController {

    private final HerbAdminQueryService herbAdminQueryService;

    @GetMapping()
    @Operation(summary = "허브 목록 조회", description = "관리자가 허브 목록을 조회합니다.")
    public ResponseEntity<HerbAdminResponseDTO> getHerbList(
            @Parameter(description = "검색 키워드") @RequestParam String keyword,
            @Parameter(description = "허브 필터링 조건") @Valid @ModelAttribute HerbFilteringConditionDTO herbFilteringConditionDTO,
            @Parameter(description = "허브 정렬 조건") @ModelAttribute HerbSort herbSort,
            Pageable pageable
    ) {

        log.info("keyword:{}", keyword);
        log.info("herbFilteringConditionDTO:{}", herbFilteringConditionDTO);
        log.info("herbSort:{}", herbSort);
        log.info("pageable:{}", pageable);

        return ResponseEntity.ok(herbAdminQueryService.getHerbList(
                keyword,
                pageable,
                herbFilteringConditionDTO,
                herbSort
        ));

    }

}
