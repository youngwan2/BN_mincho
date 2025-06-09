package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.question.QuestionService;
import com.mincho.herb.domain.qna.application.questionImage.QuestionImageService;
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
@Tag(name = "QnA 이미지", description = "QnA 이미지 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/qnas")
public class QuestionImageController {

    private final QuestionImageService questionImageService;
    private final QuestionService questionService;

    @Operation(summary = "QnA 이미지 수정", description = "QnA 이미지를 수정합니다. 새로운 이미지를 추가하고 삭제할 이미지를 지정할 수 있습니다.")
    @PatchMapping("/{qnaId}/images")
    public ResponseEntity<Void> update(
                @Parameter(description = "새로 추가할 이미지 파일 목록") @RequestPart(value = "images", required = false) List<MultipartFile> images,
                @Parameter(description = "삭제할 이미지 URL 목록") @RequestPart(value = "deletedImageUrls", required = false) List<String> imageUrlsToDelete,
                @PathVariable Long qnaId
            ){
        log.info("QnA 이미지 수정 요청: qnaId={}, 추가할 images={}, 삭제할 imageUrls={}", qnaId, images, imageUrlsToDelete);
        questionImageService.imageUpdate(imageUrlsToDelete, qnaId); // 이미지 URL 목록을 전달하여 해당 이미지들을 삭제
        questionImageService.imageUpload(images, qnaId); // 새 이미지를 업로드
        return ResponseEntity.noContent().build();
    }
}
