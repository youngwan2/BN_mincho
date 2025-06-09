package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.answer.AnswerService;
import com.mincho.herb.domain.qna.application.answerImage.AnswerImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Tag(name = "답변 이미지", description = "답변 이미지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/qnas")
public class AnswerImageController {

    private final AnswerImageService answerImageService;
    private final AnswerService answerService;

    @Operation(summary = "답변 이미지 수정", description = "답변 이미지를 수정합니다. 새로운 이미지를 추가하고 삭제할 이미지를 지정할 수 있습니다.")
    @PatchMapping("/{qnaId}/answers/{answerId}/images")
    public ResponseEntity<Void> update(
                @Parameter(description = "새로 추가할 이미지 파일 목록") @RequestPart(value = "images", required = false) List<MultipartFile> images,
                @Parameter(description = "삭제할 이미지 URL 목록") @RequestPart(value = "deletedImageUrls", required = false) List<String> imageUrlsToDelete,
                @PathVariable Long answerId
            ){

        log.info("답변 이미지 수정 요청: answerId={}, 추가할 images={}, 삭제할 imageUrls={}", answerId, images, imageUrlsToDelete);
        answerImageService.imageUpdate(imageUrlsToDelete, answerId); // 이미지 URL 목록을 전달하여 해당 이미지들을 삭제
        answerImageService.imageUpload(images, answerId); // 새 이미지를 업로드
        return ResponseEntity.noContent().build();
    }
}
