package com.mincho.herb.domain.qna.repository.questionCategory;

import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionCategoryJpaRepository extends JpaRepository<QuestionCategoryEntity, Long> {
    Optional<QuestionCategoryEntity> findByName(String name);
}
