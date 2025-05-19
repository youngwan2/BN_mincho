package com.mincho.herb.domain.qna.repository.qnaImage;

import com.mincho.herb.domain.qna.entity.QnaImageEntity;

import java.util.List;

public interface QnaImageRepository {

    QnaImageEntity save(QnaImageEntity qnaImageEntity);
    void deleteByQnaId(Long qnaId);
    void deleteByImageUrlIn(List<String> imageUrls);
    List<String> findAllImageUrlsByQnaId(Long qnaId);
}
