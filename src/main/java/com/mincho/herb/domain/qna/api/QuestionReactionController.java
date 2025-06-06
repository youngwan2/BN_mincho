package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.questionReaction.QuestionReactionService;
import com.mincho.herb.domain.qna.dto.QuestionReactionRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community")
@Tag(name = "Question Reaction", description = "질문 좋아요/싫어요 관련 API")
public class QuestionReactionController {

    private final QuestionReactionService questionReactionService;

    @PostMapping("questions/{questionId}/reactions")
    @Operation(summary = "질문에 반응 추가", description = "질문에 좋아요/싫어요를 추가합니다. 같은 반응을 다시 요청하면 취소됩니다.")
    public ResponseEntity<Void> addReaction(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @Parameter(description = "반응 정보(LIKE 또는 DISLIKE)", required = true) @Valid @RequestBody QuestionReactionRequestDTO requestDTO
    ) {
        questionReactionService.reactToQuestion(questionId, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("questions/{questionId}/reactions")
    @Operation(summary = "질문에 대한 반응 취소", description = "질문에 대한 자신의 좋아요/싫어요 반응을 취소합니다.")
    public ResponseEntity<Void> cancelReaction(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId
    ) {
        questionReactionService.cancelReaction(questionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("questions/{questionId}/reactions/count")
    @Operation(summary = "질문의 반응 개수 조회", description = "질문의 좋아요 또는 싫어요 개수를 조회합니다.")
    public ResponseEntity<Long> getReactionCount(
            @Parameter(description = "질문 ID", required = true) @PathVariable Long questionId,
            @Parameter(description = "반응 타입(LIKE 또는 DISLIKE)", required = true) @RequestParam String type
    ) {
        Long count = questionReactionService.countReactions(questionId, type);
        return ResponseEntity.ok(count);
    }
}
