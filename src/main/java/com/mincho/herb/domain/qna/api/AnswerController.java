package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.answer.AnswerService;
import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AnswerController {
        private final AnswerService answerService;


        // 답변 생성
        @PostMapping("qna/{qnaId}/answers")
        public ResponseEntity<Void> create(
                @Valid @RequestPart AnswerRequestDTO requestDTO,
                @RequestPart List<MultipartFile> images,
                @PathVariable Long qnaId
                ){

            answerService.create(qnaId, requestDTO, images);

            return ResponseEntity.status(201).build();
        }

        // 답변 수정

        // 답변 삭제
}
