package com.mincho.herb.domain.herb.api;


import com.mincho.herb.domain.herb.application.herb.HerbManageService;
import com.mincho.herb.domain.herb.dto.HerbCreateRequestDTO;
import com.mincho.herb.domain.herb.dto.HerbUpdateRequestDTO;
import com.mincho.herb.global.response.success.HttpSuccessType;
import com.mincho.herb.global.response.success.SuccessResponse;
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
@RequestMapping("/api/v1/herbs")
public class HerbManageController {
    private final HerbManageService herbManageService;



    // 허브 정보 추가
    @PostMapping()
    public ResponseEntity<?> createHerb(
            @Valid @RequestPart(value = "herb") HerbCreateRequestDTO herbCreateRequestDTO,
            @RequestPart(value ="image",  required = false) List<MultipartFile> imageFiles
            ){

        log.info("herbDTO: {}", herbCreateRequestDTO);

        herbManageService.createHerb(herbCreateRequestDTO, imageFiles);

        return new SuccessResponse<>().getResponse(200,"정상적으로 등록되었습니다.", HttpSuccessType.OK);
    }

    // 허브 정보 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateHerb(
            @PathVariable Long id,
            @Valid @RequestPart(value = "herb") HerbUpdateRequestDTO herbUpdateRequestDTO,
            @RequestPart(value = "image", required = false) List<MultipartFile> imageFiles){
        herbManageService.updateHerb(herbUpdateRequestDTO, imageFiles , id);
        return new SuccessResponse<>().getResponse(200,"정상적으로 수정되었습니다.", HttpSuccessType.OK);
    }


    // 허브 정보 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHerb(@PathVariable Long id){

        herbManageService.removeHerb(id);
        return new SuccessResponse<>().getResponse(200, "성공적으로 삭제처리 되었습니다.", HttpSuccessType.OK);
    }


    // 허브 데이터 초기화
    @PostMapping("/settings")
    public ResponseEntity<?> init() throws IOException {
        herbManageService.insertMany();
        return new SuccessResponse<>().getResponse(200, "성공적으로 추가되었습니다.", HttpSuccessType.OK);
    }
}
