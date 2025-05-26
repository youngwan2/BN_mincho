package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.answer.AnswerService;
import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "QnA Answer", description = "QnA 답변 관련 API")
public class AnswerController {
    private final AnswerService answerService;

    @PostMapping("qna/{qnaId}/answers")
    @Operation(summary = "답변 생성", description = "QnA에 답변을 생성합니다.")
    public ResponseEntity<Void> create(
            @Parameter(description = "QnA ID", required = true) @PathVariable Long qnaId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "답변 생성 요청 DTO", required = true)
            @Valid @RequestPart(value = "answer") AnswerRequestDTO requestDTO,
            @Parameter(description = "답변 이미지 파일 목록", required = false) @RequestPart(value = "image", required = false) List<MultipartFile> images
    ) {
        answerService.create(qnaId, requestDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("qna/{qnaId}/answers/{answerId}")
    @Operation(summary = "답변 수정", description = "QnA 답변을 수정합니다.")
    public ResponseEntity<Void> update(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "답변 수정 요청 DTO", required = true)
            @Valid @RequestBody AnswerRequestDTO requestDTO
    ) {
        answerService.update(answerId, requestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("qna/{qnaId}/answers/{answerId}")
    @Operation(summary = "답변 삭제", description = "QnA 답변을 삭제합니다.")
    public ResponseEntity<Void> delete(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId
    ) {
        answerService.delete(answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("qna/{qnaId}/answers/{answerId}/adopt")
    @Operation(summary = "답변 채택", description = "QnA 답변을 채택 처리합니다.")
    public ResponseEntity<Void> adopt(
            @Parameter(description = "답변 ID", required = true) @PathVariable Long answerId
    ) {
        answerService.adopt(answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
