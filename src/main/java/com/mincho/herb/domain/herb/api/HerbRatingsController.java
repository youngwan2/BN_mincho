package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.application.herbRatings.HerbRatingsService;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.dto.HerbRatingsRequestDTO;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs/ratings")
public class HerbRatingsController {

    private final HerbRatingsService herbRatingsService;
    private final HerbUserQueryService herbUserQueryService;

    // 평점 조회
    @GetMapping
    public ResponseEntity<?> getRatings(@RequestParam("herbName") @NotBlank(message = "herbName은 필수입니다.") String herbName) {
        List<HerbRatings> herbRatings = herbRatingsService.getHerbRatings(
                herbUserQueryService.getHerbByHerbName(herbName)
        );
        return new SuccessResponse<>().getResponse(200, "조회에 성공하였습니다.", HttpSuccessType.OK, herbRatings);
    }

    // 평점 등록
    @PostMapping
    public ResponseEntity<Map<String, String>> addScore(
            @RequestParam("herbName") @NotBlank(message = "herbName은 필수입니다.") String herbName,
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
