package com.mincho.herb.domain.qna.api;


import com.mincho.herb.domain.qna.application.qna.QnaService;
import com.mincho.herb.domain.qna.application.qnaImage.QnaImageService;
import com.mincho.herb.domain.qna.dto.QnaImageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/qna")
public class QnaImageController {

    private final QnaImageService qnaImageService;
    private final QnaService qnaService;

    
    // 이미지 수정
    @PatchMapping("/{qnaId}/image")
    public ResponseEntity<QnaImageResponseDTO> update(
                @RequestPart("image") List<MultipartFile> images,
                @RequestPart("imageUrl") List<String> imageUrls,
                @PathVariable Long qnaId
            ){

        qnaImageService.imageUpdate(imageUrls, qnaId);
        qnaImageService.imageUpload(images, qnaId );
        return ResponseEntity.noContent().build();
    }
}
