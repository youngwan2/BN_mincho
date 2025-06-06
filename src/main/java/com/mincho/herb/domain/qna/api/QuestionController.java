package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.question.QuestionService;
import com.mincho.herb.domain.qna.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "질문 관리", description = "질문(QnA) 관련 API")
public class QuestionController {

    private final QuestionService questionService;


    // 질문 생성
    @Operation(summary = "질문 생성", description = "새로운 질문을 생성합니다.")
    @PostMapping("/community/qna")
    public ResponseEntity<Void> create(
            @Parameter(description = "질문 정보") @RequestPart(value = "question") QuestionRequestDTO requestDTO,
            @Parameter(description = "첨부 이미지 파일 (선택사항)") @RequestPart(value ="images", required = false) List<MultipartFile> images
            ) {
                log.info("requestDTO: {}", requestDTO);
                log.info("images: {}", images);
//                questionService.create(requestDTO, images);
                return ResponseEntity.status(201).build();
    }

    // 질문 수정
    @Operation(summary = "질문 수정", description = "기존 질문을 수정합니다.")
    @PutMapping("/community/qna/{qnaId}")
    public ResponseEntity<Void> update(
            @Parameter(description = "수정할 질문 정보") @RequestBody QuestionRequestDTO requestDTO,
            @Parameter(description = "수정할 질문 ID") @PathVariable Long qnaId
    ) {
        questionService.update(qnaId, requestDTO);
        return ResponseEntity.noContent().build();
    }

    // 질문 삭제
    @Operation(summary = "질문 삭제", description = "질문을 삭제합니다.")
    @DeleteMapping("/community/qna/{qnaId}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 질문 ID") @PathVariable Long qnaId
    ){
        questionService.delete(qnaId);

        return ResponseEntity.noContent().build();
    }

    // 질문 목록조회
    @Operation(summary = "질문 목록 조회", description = "질문 목록을 조건에 따라 페이징하여 조회합니다.")
    @GetMapping("/community/qna")
    public ResponseEntity<QuestionResponseDTO> getQnaList(
            @Parameter(description = "검색 조건") @ModelAttribute QuestionSearchConditionDTO questionSearchConditionDTO,
            @Parameter(description = "페이징 정보") Pageable pageable
            ){

        log.info("condition{}", questionSearchConditionDTO);
        log.info("pageInfo:{}", pageable);

        QuestionResponseDTO responseDTO= questionService.getAllBySearchCondition(questionSearchConditionDTO, pageable);

        return ResponseEntity.ok(responseDTO);
    }

    // 질문 상세 조회
    @Operation(summary = "질문 상세 조회", description = "질문 ID로 상세 정보를 조회합니다.")
    @GetMapping("/community/qna/{qnaId}")
    public ResponseEntity<QuestionDTO> getQna(
            @Parameter(description = "조회할 질문 ID") @PathVariable Long qnaId
    ){
        return ResponseEntity.ok(questionService.getById(qnaId));
    }

    // 사용자별 질문 목록 조회
    @Operation(summary = "사용자별 질문 목록 조회", description = "특정 사용자가 작성한 질문 목록을 페이징하여 조회합니다. 비공개 질문은 제외됩니다.")
    @GetMapping("/users/{userId}/qna")
    public ResponseEntity<UserQuestionResponseDTO> getQnaListByUserId(
            @Parameter(description = "조회할 사용자 ID", required = true) @PathVariable Long userId,
            @Parameter(description = "페이징 정보") Pageable pageable
    ) {
        UserQuestionResponseDTO responseDTO = questionService.getAllByUserId(userId, pageable);
        return ResponseEntity.ok(responseDTO);
    }
}
