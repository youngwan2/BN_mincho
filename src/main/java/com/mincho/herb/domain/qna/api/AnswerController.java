package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.answer.AnswerService;
import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
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
public class AnswerController {
    private final AnswerService answerService;


    // 답변 생성
    @PostMapping("qna/{qnaId}/answers")
    public ResponseEntity<Void> create(
            @Valid @RequestPart(value = "answer") AnswerRequestDTO requestDTO,
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            @PathVariable Long qnaId
    ){
        answerService.create(qnaId, requestDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    // 답변 수정
    @PatchMapping("qna/{qnaId}/answers/{answerId}")
    public ResponseEntity<Void> update(
            @Valid @RequestBody AnswerRequestDTO requestDTO,
            @PathVariable Long answerId
    ){
        answerService.update(answerId, requestDTO);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 답변 삭제
    @DeleteMapping("qna/{qnaId}/answers/{answerId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long answerId
    ){

        answerService.delete(answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 답변 채택
    @PatchMapping("qna/{qnaId}/answers/{answerId}/adopt")
    public ResponseEntity<Void> adopt(
            @PathVariable Long answerId
    ){
        answerService.adopt(answerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
