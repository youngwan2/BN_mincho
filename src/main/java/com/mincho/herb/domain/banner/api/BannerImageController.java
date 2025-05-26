package com.mincho.herb.domain.banner.api;

import com.mincho.herb.domain.banner.application.BannerImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/banners/images")
@Tag(name = "배너 이미지 API", description = "배너 이미지 업로드, 수정, 삭제 및 presigned URL 생성을 위한 API")
public class BannerImageController {

    private final BannerImageService bannerImageService;

    @PostMapping
    @Operation(
            summary = "배너 이미지 업로드",
            description = "배너 이미지를 업로드하고 이미지 URL을 반환합니다."
    )
    public ResponseEntity<String> uploadBannerImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {

        String imageUrl = bannerImageService.uploadBannerImage(file);
        return ResponseEntity.ok(imageUrl);
    }

    @PutMapping("/{url}")
    @Operation(
            summary = "배너 이미지 수정",
            description = "기존 이미지 키를 기반으로 이미지를 수정합니다."
    )
    public ResponseEntity<String> updateBannerImage(
            @Parameter(description = "기존 이미지의 url", required = true)
            @PathVariable String url,
            @Parameter(description = "수정할 이미지 파일", required = true)
            @RequestParam("file") MultipartFile file) {

        String updatedImageUrl = bannerImageService.updateBannerImage(url, file);
        return ResponseEntity.ok(updatedImageUrl);
    }

    @DeleteMapping("/{url}")
    @Operation(
            summary = "배너 이미지 삭제",
            description = "이미지 키를 기반으로 배너 이미지를 삭제합니다."
    )
    public ResponseEntity<Void> deleteBannerImage(
            @Parameter(description = "삭제할 이미지의 키", required = true)
            @PathVariable String url) {

        bannerImageService.deleteBannerImage(url);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/presigned-url")
    @Operation(
            summary = "Presigned URL 생성",
            description = "S3에 이미지를 업로드하기 위한 presigned URL을 생성합니다."
    )
    public ResponseEntity<String> generatePresignedUrl(
            @Parameter(description = "파일 이름", required = true) @RequestParam String fileName,
            @Parameter(description = "파일 MIME 타입", required = true) @RequestParam String contentType,
            @Parameter(description = "URL 유효 시간(분)", required = true) @RequestParam int duration) {

        String presignedUrl = bannerImageService.generatePresignedUrl(fileName, contentType, duration);
        return ResponseEntity.ok(presignedUrl);
    }
}
