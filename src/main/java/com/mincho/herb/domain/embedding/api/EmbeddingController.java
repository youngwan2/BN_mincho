package com.mincho.herb.domain.embedding.api;

import com.mincho.herb.domain.embedding.application.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class EmbeddingController {
    private final EmbeddingService embeddingService;
    private final ChatModel chatModel;


    @PostMapping("/init-embedding")
    public ResponseEntity<?> embeddingRequest(){
        embeddingService.embedAllHerbsToPgVector();

        return ResponseEntity.created(null).build();

    }
}
