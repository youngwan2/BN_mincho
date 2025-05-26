package com.mincho.herb.domain.herb.api;


import com.mincho.herb.domain.herb.application.herb.HerbAdminService;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/herbs")
@Tag(name = "Herb Admin", description = "관리자 허브 관리 관련 API")
public class HerbAdminController {
    private final HerbAdminService herbAdminService;


    // 허브 정보 추가
    @PostMapping()
    @Operation(summary = "허브 정보 추가", description = "새로운 허브 정보를 추가합니다.")
    public ResponseEntity<?> createHerb(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "허브 생성 요청 DTO", required = true)
            @Valid @RequestPart(value = "herb") HerbCreateRequestDTO herbCreateRequestDTO,
            @Parameter(description = "허브 이미지 파일 목록") @RequestPart(value ="image",  required = false) List<MultipartFile> imageFiles
            ){

        log.info("herbDTO: {}", herbCreateRequestDTO);

        herbAdminService.createHerb(herbCreateRequestDTO, imageFiles);

        return new SuccessResponse<>().getResponse(200,"정상적으로 등록되었습니다.", HttpSuccessType.OK);
    }

    // 허브 정보 수정
    @PutMapping("/{id}")
    @Operation(summary = "허브 정보 수정", description = "기존 허브 정보를 수정합니다.")
    public ResponseEntity<?> updateHerb(
            @Parameter(description = "허브 ID", required = true) @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "허브 수정 요청 DTO", required = true)
            @Valid @RequestPart(value = "herb") HerbUpdateRequestDTO herbUpdateRequestDTO,
            @Parameter(description = "허브 이미지 파일 목록") @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles){
        herbAdminService.updateHerb(herbUpdateRequestDTO, imageFiles , id);
        return new SuccessResponse<>().getResponse(200,"정상적으로 수정되었습니다.", HttpSuccessType.OK);
    }


    // 허브 정보 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "허브 정보 삭제", description = "허브 정보를 삭제합니다.")
    public ResponseEntity<?> deleteHerb(
            @Parameter(description = "허브 ID", required = true) @PathVariable Long id){

        herbAdminService.removeHerb(id);
        return new SuccessResponse<>().getResponse(200, "성공적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }


    // 허브 데이터 초기화
    @PostMapping("/settings")
    @Operation(summary = "허브 데이터 초기화", description = "허브 데이터를 초기화합니다.")
    public ResponseEntity<?> init() throws IOException {
        herbAdminService.insertMany();
        return new SuccessResponse<>().getResponse(200, "성공적으로 추가되었습니다.", HttpSuccessType.OK);
    }
}
