package com.mincho.herb.domain.herb.api;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.application.herbSummary.HerbSummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/herbs")
public class HerbSummaryController {

    private final HerbSummaryService herbSummaryService;

    @GetMapping()
    public ResponseEntity<Map<String,String>> insetMany()  {
        try {
        herbSummaryService.insertMany();
        } catch (IOException ex){
            log.error(ex.getMessage());
            throw new CustomHttpException(HttpErrorCode.INTERNAL_SERVER_ERROR, "JSON 파일 직렬화 실패");
        }
        return new SuccessResponse<>().getResponse(200, "성공 하였습니다.", HttpSuccessType.OK);
        }
}
