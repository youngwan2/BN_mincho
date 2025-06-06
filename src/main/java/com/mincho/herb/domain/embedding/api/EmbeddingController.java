package com.mincho.herb.domain.embedding.api;

import com.mincho.herb.domain.embedding.application.EmbeddingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "Embedding", description = "임베딩 벡터 관련 API")
public class EmbeddingController {
    private final EmbeddingService embeddingService;
    private final ChatModel chatModel;

    @PostMapping("/init-embedding")
    @Operation(summary = "임베딩 초기화", description = "모든 약초 데이터를 PGVector에 임베딩합니다.")
    public ResponseEntity<?> embeddingRequest() {
        embeddingService.embedAllHerbsToPgVector();

        return ResponseEntity.created(null).build();
    }


    @GetMapping("/herbs-embedding")
    @Operation(summary = "약초 임베딩 조회", description = "모든 약초의 임베딩 벡터를 조회합니다.")
    public ResponseEntity<?> getAllHerbsEmbedding() {
        return ResponseEntity.ok(embeddingService.getAllHerbsEmbedding());
    }
}
