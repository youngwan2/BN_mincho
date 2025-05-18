package com.mincho.herb.domain.herb.api;


import com.mincho.herb.domain.herb.application.herb.HerbQueryService;
import com.mincho.herb.domain.herb.application.herbRatings.HerbRatingsService;
import com.mincho.herb.domain.herb.domain.HerbRatings;
import com.mincho.herb.domain.herb.dto.HerbRatingsRequestDTO;
import com.mincho.herb.global.config.error.ErrorResponse;
import com.mincho.herb.global.config.error.HttpErrorType;
import com.mincho.herb.global.config.success.HttpSuccessType;
import com.mincho.herb.global.config.success.SuccessResponse;
import com.mincho.herb.global.util.CommonUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final HerbQueryService herbQueryService;
    private final CommonUtils commonUtils;

    @GetMapping()
    ResponseEntity<?> getRatings(@RequestParam("herbName") String herbName){
        if(herbName.isEmpty()){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. herbName은 필수입니다..", HttpErrorType.BAD_REQUEST);
        }

        List<HerbRatings> herbRatings = herbRatingsService.getHerbRatings(herbQueryService.getHerbByHerbName(herbName));

        return new SuccessResponse<>().getResponse(200, "조회에 성공하였습니다.", HttpSuccessType.OK, herbRatings);
    }

    @PostMapping()
    ResponseEntity<Map<String,String>> addScore(@RequestParam("herbName") String herbName, @Valid @RequestBody HerbRatingsRequestDTO herbRatingsRequestDTO, BindingResult result){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("username: {}", email);

        if(!commonUtils.emailValidation(email)) {
            return new ErrorResponse().getResponse(401, "인증된 유저가 아닙니다.", HttpErrorType.UNAUTHORIZED);
        }
        if(herbName.isEmpty()){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. herbName은 필수입니다..", HttpErrorType.BAD_REQUEST);
        }
        if(result.hasErrors()){
            return new ErrorResponse().getResponse(400, commonUtils.extractErrorMessage(result), HttpErrorType.BAD_REQUEST);
        }

        herbRatingsService.addScore(HerbRatings.builder()
                .score(herbRatingsRequestDTO.getScore())
                .build(),
                herbName,
                email
        );
        return new SuccessResponse<>().getResponse(201, "평점 등록에 성공하였습니다.", HttpSuccessType.CREATED );
    }
}
