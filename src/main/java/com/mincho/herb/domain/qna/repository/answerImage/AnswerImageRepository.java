package com.mincho.herb.domain.qna.repository.answerImage;

import com.mincho.herb.domain.qna.entity.AnswerImageEntity;

import java.util.List;

public interface AnswerImageRepository {

    AnswerImageEntity save(AnswerImageEntity AnswerImageEntity);

    List<String> findAllImageUrlsByAnswerId(Long answerId);


    void deleteByAnswerId(Long answerId);

    void deleteByImageUrlIn(List<String> imageUrls);
}
