package com.mincho.herb.domain.herb.api;

import com.mincho.herb.domain.embedding.dto.RecommendHerbResponseDTO;
import com.mincho.herb.domain.embedding.dto.RecommendHerbsDTO;
import com.mincho.herb.domain.herb.application.herb.HerbRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Herb Recommendation", description = "허브 추천 관련 API")
public class HerbRecommendationController {

    private final HerbRecommendationService herbRecommendationService;

    // 약초 추천
    @GetMapping("/recommend")
    @Operation(summary = "허브 추천", description = "사용자의 메시지를 기반으로 허브를 추천합니다.")
    public ResponseEntity<?> herbRecommendation(
            @Parameter(description = "사용자 메시지", required = true) @RequestParam(value = "message") String message
    ) {

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
