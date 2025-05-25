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

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/herbs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class HerbAdminQueryController {

    private final HerbAdminQueryService herbAdminQueryService;



    @GetMapping()
    public ResponseEntity<HerbAdminResponseDTO> getHerbList(
            @RequestParam String keyword,
            @Valid @ModelAttribute HerbFilteringConditionDTO herbFilteringConditionDTO,
            @ModelAttribute HerbSort herbSort,
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
