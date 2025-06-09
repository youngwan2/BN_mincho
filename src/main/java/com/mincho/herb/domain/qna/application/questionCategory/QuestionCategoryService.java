package com.mincho.herb.domain.qna.application.questionCategory;

import com.mincho.herb.domain.qna.dto.QuestionCategoryCreateDTO;
import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;

import java.util.List;

public interface QuestionCategoryService {

    /**
     * 모든 질문 카테고리 목록을 조회합니다.
     *
     * @return 질문 카테고리 목록
     */
    List<QuestionCategoryDTO> getAllCategories();

    /**
     * 새로운 질문 카테고리를 생성합니다. (관리자 전용)
     *
     * @param categoryDTO 카테고리 생성 정보
     * @return 생성된 카테고리 정보
     */
    QuestionCategoryDTO createCategory(QuestionCategoryCreateDTO categoryDTO);

    /**
     * 기존 질문 카테고리를 수정합니다. (관리자 전용)
     *
     * @param categoryId 수정할 카테고리 ID
     * @param categoryDTO 카테고리 수정 정보
     * @return 수정된 카테고리 정보
     */
    QuestionCategoryDTO updateCategory(Long categoryId, QuestionCategoryCreateDTO categoryDTO);

    /**
     * 질문 카테고리를 삭제합니다. (관리자 전용)
     *
     * @param categoryId 삭제할 카테고리 ID
     */
    void deleteCategory(Long categoryId);

    /**
     * 카테고리를 기본 값으로 초기화합���다. (관리자 전용)
     * 기존 카테고리에 연결된 질문이 없는 경우에만 초기화가 가능합니다.
     *
     * @return 초기화된 카테고리 목록
     */
    List<QuestionCategoryDTO> initializeCategories();
}
