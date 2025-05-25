package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.qna.QnaService;
import com.mincho.herb.domain.qna.dto.QnaDTO;
import com.mincho.herb.domain.qna.dto.QnaRequestDTO;
import com.mincho.herb.domain.qna.dto.QnaResponseDTO;
import com.mincho.herb.domain.qna.dto.QnaSearchConditionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;


    // 질문 생성
    @PostMapping()
    public ResponseEntity<Void> create(
            @RequestPart(value = "question") QnaRequestDTO requestDTO,
            @RequestPart(value ="image", required = false) List<MultipartFile> images
            ) {
                qnaService.create(requestDTO, images);
                return ResponseEntity.status(201).build();
    }

    // 질문 수정
    @PutMapping("/{qnaId}")
    public ResponseEntity<Void> update(
            @RequestBody QnaRequestDTO requestDTO,
            @PathVariable Long qnaId
    ) {
        qnaService.update(qnaId, requestDTO);
        return ResponseEntity.noContent().build();
    }

    // 질문 삭제
    @DeleteMapping("/{qnaId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long qnaId
    ){
        qnaService.delete(qnaId);

        return ResponseEntity.noContent().build();
    }

    // 질문 목록조회
    @GetMapping()
    public ResponseEntity<QnaResponseDTO> getQnaList(
            @ModelAttribute QnaSearchConditionDTO qnaSearchConditionDTO,
            Pageable pageable
            ){

        log.info("condition{}", qnaSearchConditionDTO);
        log.info("pageInfo:{}", pageable);

        QnaResponseDTO responseDTO= qnaService.getAllBySearchCondition(qnaSearchConditionDTO, pageable);

        return ResponseEntity.ok(responseDTO);
    }

    // 질문 상세 조회
    @GetMapping("/{qnaId}")
    public ResponseEntity<QnaDTO> getQna(
            @PathVariable Long qnaId
    ){
        return ResponseEntity.ok(qnaService.getById(qnaId));
    }
}
