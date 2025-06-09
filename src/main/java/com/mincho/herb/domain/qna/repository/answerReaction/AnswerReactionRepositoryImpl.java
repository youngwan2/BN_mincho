package com.mincho.herb.domain.qna.repository.answerReaction;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AnswerReactionRepositoryImpl implements AnswerReactionRepository {

    private final AnswerReactionJpaRepository answerReactionJpaRepository;

    @Override
    public AnswerReactionEntity save(AnswerReactionEntity reactionEntity) {
        return answerReactionJpaRepository.save(reactionEntity);
    }

    @Override
    public AnswerReactionEntity findByUserAndAnswer(UserEntity user, AnswerEntity answer) {
        return answerReactionJpaRepository.findByUserAndAnswer(user, answer).orElse(null);
    }

    @Override
    public Long countByAnswerIdAndReactionType(Long answerId, AnswerReactionEntity.ReactionType reactionType) {
        return answerReactionJpaRepository.countByAnswerIdAndReactionType(answerId, reactionType);
    }

    @Override
    public void deleteByUserAndAnswer(UserEntity user, AnswerEntity answer) {
        answerReactionJpaRepository.deleteByUserAndAnswer(user, answer);
    }

    @Override
    public void delete(AnswerReactionEntity reactionEntity) {
        answerReactionJpaRepository.delete(reactionEntity);
    }
}
