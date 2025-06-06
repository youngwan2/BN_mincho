package com.mincho.herb.domain.qna.application.questionCategory;

import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;

import java.util.List;

public interface QuestionCategoryService {
    /**
     * 모든 QnA 카테고리 목록을 조회합니다.
     * @return 카테고리 DTO 목록
     */
    List<QuestionCategoryDTO> getAllCategories();

    /**
     * 카테고리를 기본값으로 초기화합니다.
     * @return 초기화된 카테고리 DTO 목록
     */
    List<QuestionCategoryDTO> initializeCategories();
}
