package com.mincho.herb.domain.qna.repository.questionImage;

import com.mincho.herb.domain.qna.entity.QuestionImageEntity;

import java.util.List;

public interface QuestionImageRepository {

    QuestionImageEntity save(QuestionImageEntity questionImageEntity);
    void deleteByQnaId(Long qnaId);
    void deleteByImageUrlIn(List<String> imageUrls);
    List<String> findAllImageUrlsByQnaId(Long qnaId);
}
