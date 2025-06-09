package com.mincho.herb.domain.qna.repository.questionCategory;

import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;

import java.util.List;

public interface QuestionCategoryRepository {
    QuestionCategoryEntity findById(Long id);
    List<QuestionCategoryEntity> findAll();
    QuestionCategoryEntity save(QuestionCategoryEntity categoryEntity);
    void delete(QuestionCategoryEntity categoryEntity);
    boolean existsByName(String name);
    boolean hasRelatedQuestions(Long categoryId);
}
