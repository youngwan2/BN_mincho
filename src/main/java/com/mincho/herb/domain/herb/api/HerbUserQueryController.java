package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.herb.application.herb.HerbUserQueryService;
import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.global.response.error.ErrorResponse;
import com.mincho.herb.global.response.error.HttpErrorType;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Herb User Query", description = "사용자 허브 조회 관련 API")
public class HerbUserQueryController {
    
    private final HerbUserQueryService herbUserQueryService;

    // 허브 목록 조회
    @GetMapping()
    @Operation(summary = "허브 목록 조회", description = "조건에 따라 허브 목록을 조회합니다.")
    public ResponseEntity<?> getHerbs(
            @Parameter(description = "월") @RequestParam("month") String month,
            @Parameter(description = "한방명") @RequestParam("bneNm") String bneNm,
            @Parameter(description = "약초명") @RequestParam("cntntsSj") String cntntsSj,
            @Parameter(description = "정렬 기준") @RequestParam("sort") String sort,
            @Parameter(description = "정렬 방향") @RequestParam("order") String order,
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
    @Operation(summary = "허브 상세 조회", description = "특정 허브의 상세 정보를 조회합니다.")
    public ResponseEntity<?> getHerbDetails(
            @Parameter(description = "허브 ID", required = true) @PathVariable Long id
    ){
        HerbDetailResponseDTO herbDetails = herbUserQueryService.getHerbDetails(id);

        return new SuccessResponse<HerbDetailResponseDTO>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbDetails);
    }

    // 허브 랜덤 조회
    @GetMapping("/{id}/random")
    @Operation(summary = "허브 랜덤 조회", description = "특정 허브와 관련된 랜덤 허브를 조회합니다.")
    public ResponseEntity<?> getHerbRandom(
            @Parameter(description = "허브 ID", required = true) @PathVariable Long id
    ){
        List<HerbDTO> herbs = herbUserQueryService.getRandomHerbs(id);

        return new SuccessResponse<List<HerbDTO>>().getResponse(200, "성공적으로 조회 되었습니다.", HttpSuccessType.OK, herbs);
    }


    // 이달의 개화 약초(허브)
    @GetMapping("/blooming")
    @Operation(summary = "이달의 개화 허브 조회", description = "지정된 월에 개화하는 허브를 조회합니다.")
    public ResponseEntity<?> getHerbsBloomingThisMonth(
            @Parameter(description = "월", required = true) @RequestParam String month
    ){
        if(month == null){
            return new ErrorResponse().getResponse(400, "month 는 필수입니다.", HttpErrorType.BAD_REQUEST);
        }
        List<HerbDTO> herbs = herbUserQueryService.getHerbsBloomingThisMonth(month);
        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, herbs);
    }

    // 사람들이 많이 찾은 약초
    @GetMapping("/realtime-mostview")
    @Operation(summary = "실시간 인기 허브 조회", description = "사용자들이 많이 조회한 허브를 실시간으로 조회합니다.")
    public ResponseEntity<?> getHerbsMostview(){
        List<PopularityHerbsDTO> response = herbUserQueryService.getHerbsMostview();
        return new SuccessResponse<>().getResponse(200, "성공적으로 조회되었습니다.", HttpSuccessType.OK, response);
    }
}
