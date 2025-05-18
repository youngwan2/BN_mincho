package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.embedding.dto.RecommendHerbResponseDTO;
import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.domain.herb.application.herb.HerbRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/herbs")
public class HerbRecommendationController {

    private final HerbRecommendationService herbRecommendationService;

    // 약초 추천
    @GetMapping("/recommend")
    public ResponseEntity<?> embed(@RequestParam(value = "message") String message) {

        List<RecommendHerbsDTO> recommendHerbs = herbRecommendationService.getSimilaritySearchByRag(message);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREAN);
        RecommendHerbResponseDTO response = new RecommendHerbResponseDTO(
                "bot",
                recommendHerbs,
                LocalTime.now().format(formatter)
        );

        return ResponseEntity.ok(response);
    }
}
