package com.mincho.herb.domain.herb.api;

import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.herb.application.herbSummary.HerbSummaryService;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/herbs")
public class HerbSummaryController {

    private final HerbSummaryService herbSummaryService;

    @GetMapping()
    public ResponseEntity<?> getHerbs(@RequestParam("page") String page, @RequestParam("size") String size){
        if(page.isEmpty()){
            return new ErrorResponse().getResponse(400, "page 정보는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }
       List<HerbSummary> herbSummaries = herbSummaryService.getHerbs(Integer.parseInt(page), Integer.parseInt(size));
       return new SuccessResponse<List<HerbSummary>>().getResponse(200, "성공적으로 조회 되었습니다.",  HttpSuccessType.OK, herbSummaries);
    }












    @GetMapping("/setting-summary")
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
