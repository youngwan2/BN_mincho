package com.mincho.herb.domain.qna.repository.answer;


import com.mincho.herb.domain.qna.entity.AnswerEntity;

public interface AnswerRepository{

    AnswerEntity save(AnswerEntity answerEntity);

    AnswerEntity findById(Long id);

    AnswerEntity findByQnaId(Long qneId);
}
