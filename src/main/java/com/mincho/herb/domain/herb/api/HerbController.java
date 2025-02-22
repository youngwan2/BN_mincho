package com.mincho.herb.domain.herb.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.domain.herb.application.herb.HerbService;
import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs")
public class HerbController {
    private final HerbService herbService;

    // 허브 목록 조회
    @GetMapping()
    public ResponseEntity<?> getHerbs(@RequestParam("page") String page, @RequestParam("size") String size){
        if(page.isEmpty()){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. page 정보는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }

        List<Herb> herbSummaries = herbService.getHerbSummary(Integer.parseInt(page), Integer.parseInt(size));
        return new SuccessResponse<List<Herb>>().getResponse(200, "성공적으로 조회 되었습니다.",  HttpSuccessType.OK, herbSummaries);
    }

    // 상세 페이지 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getHerbDetails(@PathVariable Long id){
        Herb herbDetails = herbService.getHerbDetails(id);
        return new SuccessResponse<Herb>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbDetails);
    }

    // 허브 정보 추가
    @PostMapping()
    public ResponseEntity<?> createHerb(@Valid @RequestBody HerbCreateRequestDTO herbCreateRequestDTO){

        log.info("herbDTO: {}", herbCreateRequestDTO);

        herbService.createHerb(herbCreateRequestDTO);

        return new SuccessResponse<>().getResponse(200,"정상적으로 등록되었습니다.", HttpSuccessType.OK);
    }

    // 허브 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHerb(@PathVariable Long id, @Valid @RequestBody HerbUpdateRequestDTO herbUpdateRequestDTO){
        herbService.updateHerb(herbUpdateRequestDTO, id);
        return new SuccessResponse<>().getResponse(200,"정상적으로 수정되었습니다.", HttpSuccessType.OK);
    }

    // 허브 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHerb(@PathVariable Long id){

        herbService.removeHerb(id);
        return new SuccessResponse<>().getResponse(200, "성공적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }

    // 허브 데이터 초기화
    @PostMapping("/settings")
    public ResponseEntity<?> init() throws IOException {
        herbService.insertMany();
        return new SuccessResponse<>().getResponse(200, "성공적으로 추가되었습니다.", HttpSuccessType.OK);
    }
}
