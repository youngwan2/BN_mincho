package com.mincho.herb.domain.qna.repository.questionCategory;

import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;

import java.util.List;

public interface QuestionCategoryRepository {
    QuestionCategoryEntity findById(Long id);
    List<QuestionCategoryEntity> findAll();
}
