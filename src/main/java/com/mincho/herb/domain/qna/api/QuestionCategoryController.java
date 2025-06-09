package com.mincho.herb.domain.qna.api;

import com.mincho.herb.domain.qna.application.questionCategory.QuestionCategoryService;
import com.mincho.herb.domain.qna.dto.QuestionCategoryCreateDTO;
import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "카테고리 생성", description = "새로운 질문 카테고리를 생성합니다. (관리자 전용)")
    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionCategoryDTO> createCategory(
            @Parameter(description = "카테고리 정보") @RequestBody @Valid QuestionCategoryCreateDTO categoryDTO) {
        QuestionCategoryDTO createdCategory = questionCategoryService.createCategory(categoryDTO);
        return ResponseEntity.status(201).body(createdCategory);
    }

    @Operation(summary = "카테고리 수정", description = "기존 질문 카테고리를 수정합니다. (관리자 전용)")
    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<QuestionCategoryDTO> updateCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId,
            @Parameter(description = "카테고리 정보") @RequestBody @Valid QuestionCategoryCreateDTO categoryDTO) {
        QuestionCategoryDTO updatedCategory = questionCategoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @Operation(summary = "카테고리 삭제", description = "질문 카테고리를 삭제합니다. (관리자 전용)")
    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        questionCategoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "카테고리 초기화", description = "QnA 카테고리를 기본값으로 초기화합니다. (관리자 전용)")
    @PostMapping("/categories/init")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<QuestionCategoryDTO>> initializeCategories() {
        return ResponseEntity.ok(questionCategoryService.initializeCategories());
    }
}
