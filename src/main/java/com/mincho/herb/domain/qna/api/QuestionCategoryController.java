package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.questionCategory.QuestionCategoryService;
import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/community/qnas")
@RequiredArgsConstructor
@Tag(name = "QnA 카테고리", description = "QnA 카테고리 관련 API")
public class QuestionCategoryController {

    private final QuestionCategoryService questionCategoryService;

    @Operation(summary = "카테고리 목록 조회", description = "QnA에 사용 가능한 모든 카테고리 목록을 조회합니다.")
    @GetMapping("/categories")
    public ResponseEntity<List<QuestionCategoryDTO>> getCategories() {
        return ResponseEntity.ok(questionCategoryService.getAllCategories());
    }

    @Operation(summary = "카테고리 초기화", description = "QnA 카테고리를 기본값으로 초기화합니다. (관리자 전용)")
    @PostMapping("/categories/init")
    public ResponseEntity<List<QuestionCategoryDTO>> initializeCategories() {
        return ResponseEntity.ok(questionCategoryService.initializeCategories());
    }
}
