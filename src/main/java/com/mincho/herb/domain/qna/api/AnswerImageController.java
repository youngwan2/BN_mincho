package com.mincho.herb.domain.qna.api;


import com.mincho.herb.domain.qna.application.answer.AnswerService;
import com.mincho.herb.domain.qna.application.answerImage.AnswerImageService;
import com.mincho.herb.domain.qna.dto.QnaImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qna")
public class AnswerImageController {

    private final AnswerImageService answerImageService;
    private final AnswerService answerService;

    
    // 이미지 수정
    @PatchMapping("/{qnaId}/image/answer")
    public ResponseEntity<QnaImageResponseDTO> update(
                @RequestPart("image") List<MultipartFile> images,
                @RequestPart("imageUrl") List<String> imageUrls,
                @PathVariable Long qnaId
            ){

        answerImageService.imageUpdate(imageUrls, qnaId);
        answerImageService.imageUpload(images, qnaId );

        return ResponseEntity.noContent().build();
    }
}
