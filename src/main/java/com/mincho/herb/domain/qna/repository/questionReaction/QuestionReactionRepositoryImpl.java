package com.mincho.herb.domain.qna.repository.questionReaction;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionReactionRepositoryImpl implements QuestionReactionRepository {

    private final QuestionReactionJpaRepository questionReactionJpaRepository;

    @Override
    public QuestionReactionEntity save(QuestionReactionEntity reactionEntity) {
        return questionReactionJpaRepository.save(reactionEntity);
    }

    @Override
    public QuestionReactionEntity findByUserAndQuestion(UserEntity user, QuestionEntity question) {
        return questionReactionJpaRepository.findByUserAndQuestion(user, question).orElse(null);
    }

    @Override
    public Long countByQuestionIdAndReactionType(Long questionId, QuestionReactionEntity.ReactionType reactionType) {
        return questionReactionJpaRepository.countByQuestionIdAndReactionType(questionId, reactionType);
    }

    @Override
    public void deleteByUserAndQuestion(UserEntity user, QuestionEntity question) {
        questionReactionJpaRepository.deleteByUserAndQuestion(user, question);
    }

    @Override
    public void deleteAllByQuestion(QuestionEntity question) {
        questionReactionJpaRepository.deleteAllByQuestion(question);
    }

    @Override
    public void delete(QuestionReactionEntity reactionEntity) {
        questionReactionJpaRepository.delete(reactionEntity);
    }
}
