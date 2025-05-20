package com.mincho.herb.domain.qna.repository.answer;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerRepositoryImpl implements AnswerRepository {

    private final AnswerJpaRepository answerJpaRepository;

    @Override
    public AnswerEntity save(AnswerEntity answerEntity) {
        return answerJpaRepository.save(answerEntity);
    }

    public AnswerEntity findById(Long id) {
        return answerJpaRepository.findById(id).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 답변을 찾을 수 없습니다."));
    }

    @Override
    public AnswerEntity findByQnaId(Long qnaId) {
        return answerJpaRepository.findByQnaId(qnaId).orElse(null);
    }

    @Override
    public void deleteById(Long answerId) {
        answerJpaRepository.deleteById(answerId);
    }

    @Override
    public Boolean existsByQnaIdAndIdAndIsAdoptedTrue(Long qnaId, Long answerId) {
        return answerJpaRepository.existsByQnaIdAndIdAndIsAdoptedTrue(qnaId, answerId);
    }

    @Override
    public Boolean existsByQnaId(Long qnaId) {
        return answerJpaRepository.existsByQnaId(qnaId);
    }

    @Override
    public Boolean existsByQnaIdAndWriterId(Long qnaId, Long writerId) {
        return answerJpaRepository.existsByQnaIdAndWriterId(qnaId, writerId);
    }
}
