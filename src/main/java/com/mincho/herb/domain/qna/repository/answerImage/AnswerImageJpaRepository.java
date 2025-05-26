package com.mincho.herb.domain.qna.repository.answerImage;

import com.mincho.herb.domain.qna.entity.AnswerImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnswerImageJpaRepository extends JpaRepository<AnswerImageEntity, Long> {


    @Modifying
    @Query("DELETE FROM AnswerImageEntity qi WHERE qi.answer.id = :answerId")
    void deleteByAnswerId(@Param("answerId") Long answerId);

    Optional<List<AnswerImageEntity>> findAllByAnswerId(Long qndId);

    @Modifying
    @Query("DELETE FROM AnswerImageEntity qi WHERE qi.imageUrl NOT IN :urls ")
    void deleteByImageUrlIn(@Param("urls") List<String> imageUrls);
}
