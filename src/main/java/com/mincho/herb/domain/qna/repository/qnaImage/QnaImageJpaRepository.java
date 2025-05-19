package com.mincho.herb.domain.qna.repository.qnaImage;

import com.mincho.herb.domain.qna.entity.QnaImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QnaImageJpaRepository extends JpaRepository<QnaImageEntity, Long> {

    @Modifying
    @Query("DELETE FROM QnaImageEntity qi WHERE qi.qna.id = :qnaId")
    void deleteByQnaId(@Param("qnaId") Long qnaId);

    Optional<List<QnaImageEntity>> findAllByQnaId(Long qndId);

    @Modifying
    @Query("DELETE FROM QnaImageEntity qi WHERE qi.imageUrl NOT IN :urls ")
    void deleteByImageUrlIn(@Param("urls") List<String> imageUrls);
}
