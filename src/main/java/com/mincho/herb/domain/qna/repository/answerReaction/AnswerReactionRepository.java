package com.mincho.herb.domain.qna.repository.answerReaction;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity.ReactionType;
import com.mincho.herb.domain.user.entity.UserEntity;

public interface AnswerReactionRepository {

    AnswerReactionEntity save(AnswerReactionEntity reactionEntity);

    AnswerReactionEntity findByUserAndAnswer(UserEntity user, AnswerEntity answer);

    Long countByAnswerIdAndReactionType(Long answerId, ReactionType reactionType);

    void deleteByUserAndAnswer(UserEntity user, AnswerEntity answer);

    void delete(AnswerReactionEntity reactionEntity);
}
