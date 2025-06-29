package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.application.herbRatings.HerbRatingsService;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.dto.HerbRatingsRequestDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs/ratings")
@Tag(name = "Herb Ratings", description = "허브 평점 관련 API")
public class HerbRatingsController {

    private final HerbRatingsService herbRatingsService;
    private final HerbUserQueryService herbUserQueryService;

    // 평점 조회
    @GetMapping
    @Operation(summary = "허브 평점 조회", description = "특정 허브의 평점을 조회합니다.")
    public ResponseEntity<?> getRatings(
            @Parameter(description = "허브 이름", required = true) @RequestParam("herbName") @NotBlank(message = "herbName은 필수입니다.") String herbName
    ) {
        List<HerbRatings> herbRatings = herbRatingsService.getHerbRatings(
                herbUserQueryService.getHerbByHerbName(herbName)
        );
        return new SuccessResponse<>().getResponse(200, "조회에 성공하였습니다.", HttpSuccessType.OK, herbRatings);
    }

    // 평점 등록
    @PostMapping
    @Operation(summary = "허브 평점 등록", description = "특정 허브에 평점을 등록합니다.")
    public ResponseEntity<Map<String, String>> addScore(
            @Parameter(description = "허브 이름", required = true) @RequestParam("herbName") @NotBlank(message = "herbName은 필수입니다.") String herbName,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "허브 평점 요청 DTO", required = true)
            @Valid @RequestBody HerbRatingsRequestDTO herbRatingsRequestDTO
    ) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("username: {}", email);

        if (!email.contains("@")) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }

        herbRatingsService.addScore(
                HerbRatings.builder().score(herbRatingsRequestDTO.getScore()).build(),
                herbName,
                email
        );
        return new SuccessResponse<>().getResponse(201, "평점 등록에 성공하였습니다.", HttpSuccessType.CREATED);
    }
}
