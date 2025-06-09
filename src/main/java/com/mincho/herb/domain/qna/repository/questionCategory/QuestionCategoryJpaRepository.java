package com.mincho.herb.domain.qna.repository.questionCategory;

import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionCategoryJpaRepository extends JpaRepository<QuestionCategoryEntity, Long> {
    Optional<QuestionCategoryEntity> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT COUNT(q) FROM QuestionEntity q WHERE q.category.id = :categoryId")
    long countByQuestionCategoryId(@Param("categoryId") Long categoryId);
}
