package com.mincho.herb.domain.qna.repository.questionCategory;

import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryRepositoryImpl implements QuestionCategoryRepository {

    private final QuestionCategoryJpaRepository questionCategoryJpaRepository;

    @Override
    public QuestionCategoryEntity findById(Long id) {
        return questionCategoryJpaRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."));
    }

    @Override
    public List<QuestionCategoryEntity> findAll() {
        return questionCategoryJpaRepository.findAll();
    }
}
