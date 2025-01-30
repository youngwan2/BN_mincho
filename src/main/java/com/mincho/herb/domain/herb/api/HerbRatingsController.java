package com.mincho.herb.domain.herb.api;


import com.mincho.herb.common.config.SecurityConfig;
import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.common.util.ValidationUtils;
import com.mincho.herb.domain.herb.application.herbRatings.HerbRatingsService;
import com.mincho.herb.domain.herb.application.herbSummary.HerbSummaryService;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.herb.dto.RequestHerbRatingsDTO;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs/ratings")
public class HerbRatingsController {

    private final HerbRatingsService herbRatingsService;
    private final HerbSummaryService herbSummaryService;
    private final ValidationUtils validationUtils;

    @GetMapping()
    ResponseEntity<?> getRatings(@RequestParam("herbName") String herbName){
        if(herbName.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "hername 은 필수 입니다.");
        }

        List<HerbRatings> herbRatings = herbRatingsService.getHerbRatings(herbSummaryService.getHerbByHerbName(herbName));

        return new SuccessResponse<>().getResponse(200, "조회에 성공하였습니다.", HttpSuccessType.OK, herbRatings);
    }

    @PostMapping()
    ResponseEntity<Map<String,String>> addScore(@RequestParam("herbName") String herbName, @Valid @RequestBody RequestHerbRatingsDTO requestHerbRatingsDTO, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("username: {}", email);
        if(email.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"평점 등록 요청 권한이 없습니다.");
        }
        if(herbName.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "hername 은 필수 입니다.");
        }
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, validationUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        herbRatingsService.addScore(HerbRatings.builder()
                .score(requestHerbRatingsDTO.getScore())
                .build(),
                herbName,
                email
        );
        return new SuccessResponse<>().getResponse(201, "평점 등록에 성공하였습니다.", HttpSuccessType.CREATED );
    }
}
