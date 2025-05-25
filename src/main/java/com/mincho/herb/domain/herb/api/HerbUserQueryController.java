package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs")
public class HerbUserQueryController {
    
    private final HerbUserQueryService herbUserQueryService;

    // 허브 목록 조회
    @GetMapping()
    public ResponseEntity<?> getHerbs(

            @RequestParam("month") String month,
            @RequestParam("bneNm") String bneNm,
            @RequestParam("cntntsSj") String cntntsSj,
            @RequestParam("sort") String sort,
            @RequestParam("order") String order,
            Pageable pageable

    ){

        HerbFilteringRequestDTO herbFilteringRequestDTO= HerbFilteringRequestDTO.builder()
                .bneNm(bneNm)
                .month(month)
                .cntntsSj(cntntsSj)
                .build();

        HerbSort herbSort = HerbSort.builder()
                .sort(sort)
                .order(order)
                .build();

        List<HerbDTO> herbs = herbUserQueryService.getHerbs(
                pageable,
                herbFilteringRequestDTO,
                herbSort

        );

        Long totalCount = herbUserQueryService.getHerbCount(herbFilteringRequestDTO);

        int page = pageable.getPageNumber(); // 현재 페이지

        // 페이지 정보
        HerbResponseDTO herbResponseDTO = HerbResponseDTO.builder()
                .herbs(herbs)
                .nextPage(herbs.isEmpty() ? page : ++page) // 다음 페이지
                .totalCount(totalCount)
                .build();

        return ResponseEntity.ok(herbResponseDTO);
    }

    // 상세 페이지 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getHerbDetails(@PathVariable Long id){
        HerbDetailResponseDTO herbDetails = herbUserQueryService.getHerbDetails(id);

        return new SuccessResponse<HerbDetailResponseDTO>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbDetails);
    }

    // 허브 랜덤 조회
    @GetMapping("/{id}/random")
    public ResponseEntity<?> getHerbRandom(@PathVariable Long id){
        List<HerbDTO> herbs = herbUserQueryService.getRandomHerbs(id);

        return new SuccessResponse<List<HerbDTO>>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbs);
    }


    // 이달의 개화 약초(허브)
    @GetMapping("/blooming")
    public ResponseEntity<?> getHerbsBloomingThisMonth(
            @RequestParam String month
    ){
        if(month == null){
            return new ErrorResponse().getResponse(400, "month 는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }
        List<HerbDTO> herbs = herbUserQueryService.getHerbsBloomingThisMonth(month);
        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, herbs);
    }

    // 사람들이 많이 찾은 약초
    @GetMapping("/realtime-mostview")
    public ResponseEntity<?> getHerbsMostview(){
        List<PopularityHerbsDTO> response = herbUserQueryService.getHerbsMostview();
        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, response);
    }
}
