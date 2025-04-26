package com.mincho.herb.domain.herb.api;


import com.mincho.herb.common.config.error.ErrorResponse;
import com.mincho.herb.common.config.error.HttpErrorType;
import com.mincho.herb.common.config.success.HttpSuccessType;
import com.mincho.herb.common.config.success.SuccessResponse;
import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.domain.herb.application.herb.HerbService;
import com.mincho.herb.domain.herb.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> getHerbs(
            @RequestParam("page") String page,
            @RequestParam("size") String size,
            @RequestParam("month") String month,
            @RequestParam("bneNm") String bneNm,
            @RequestParam("orderBy") String orderBy
            ){

        if(page.isEmpty()){
            return new ErrorResponse().getResponse(400, "잘못된 요청입니다. page 정보는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }

        Integer pageNum = Integer.parseInt(page);
        Integer pageSize = Integer.parseInt(size);

        HerbFilteringRequestDTO herbFilteringRequestDTO= HerbFilteringRequestDTO.builder()
                .bneNm(bneNm)
                .month(month)
                .orderBy(orderBy)
                .build();

        List<HerbDTO> herbs = herbService.getHerbs(
                PageInfoDTO.builder()
                        .page(pageNum.longValue())
                        .size(pageSize.longValue())
                        .build(),
                herbFilteringRequestDTO

        );

        Long totalCount = herbService.getHerbCount(herbFilteringRequestDTO);


        HerbResponseDTO herbResponseDTO = HerbResponseDTO.builder()
                .herbs(herbs)
                .nextPage(herbs.isEmpty() ? pageNum: ++pageNum) // 다음 페이지
                .totalCount(totalCount)
                .build();

        return ResponseEntity.ok(herbResponseDTO);
    }

    // 상세 페이지 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getHerbDetails(@PathVariable Long id){
        HerbDetailResponseDTO herbDetails = herbService.getHerbDetails(id);

        return new SuccessResponse<HerbDetailResponseDTO>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbDetails);
    }

    // 허브 랜덤 조회
    @GetMapping("/{id}/random")
    public ResponseEntity<?> getHerbRandom(@PathVariable Long id){
        List<HerbDTO> herbs = herbService.getRandomHerbs(id);

        return new SuccessResponse<List<HerbDTO>>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbs);
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

    // 이달의 개화 약초(허브)
    @GetMapping("/blooming")
    public ResponseEntity<?> getHerbsBloomingThisMonth(
            @RequestParam String month
    ){
        if(month == null){
            return new ErrorResponse().getResponse(400, "month 는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }
        List<HerbDTO> herbs = herbService.getHerbsBloomingThisMonth(month);
        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, herbs);
    }
    // 허브 데이터 초기화
    @PostMapping("/settings")
    public ResponseEntity<?> init() throws IOException {
        herbService.insertMany();
        return new SuccessResponse<>().getResponse(200, "성공적으로 추가되었습니다.", HttpSuccessType.OK);
    }
}
