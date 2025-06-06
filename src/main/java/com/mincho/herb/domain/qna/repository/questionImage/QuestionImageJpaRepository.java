package com.mincho.herb.domain.qna.repository.questionImage;

import com.mincho.herb.domain.qna.entity.QuestionImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionImageJpaRepository extends JpaRepository<QuestionImageEntity, Long> {

    @Modifying
    @Query("DELETE FROM QuestionImageEntity qi WHERE qi.question.id = :questionId")
    void deleteByQnaId(@Param("questionId") Long qnaId);

    Optional<List<QuestionImageEntity>> findAllByQuestionId(Long questionId);

    @Modifying
    @Query("DELETE FROM QuestionImageEntity qi WHERE qi.imageUrl IN :urls ")
    void deleteByImageUrlIn(@Param("urls") List<String> imageUrls);
}
