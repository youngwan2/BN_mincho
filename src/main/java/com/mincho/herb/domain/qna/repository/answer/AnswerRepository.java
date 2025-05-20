package com.mincho.herb.domain.qna.repository.answer;


import com.mincho.herb.domain.qna.entity.AnswerEntity;

public interface AnswerRepository{

    AnswerEntity save(AnswerEntity answerEntity);

    AnswerEntity findById(Long id);

    AnswerEntity findByQnaId(Long qneId);

    void deleteById(Long answerId);

    Boolean existsByQnaIdAndIdAndIsAdoptedTrue(Long qnaId, Long answerId);
    Boolean existsByQnaId(Long qnaId);
    Boolean existsByQnaIdAndWriterId(Long qnaId, Long writerId);

}
