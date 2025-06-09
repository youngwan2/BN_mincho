package com.mincho.herb.domain.qna.repository.questionReaction;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity.ReactionType;
import com.mincho.herb.domain.user.entity.UserEntity;

public interface QuestionReactionRepository {

    QuestionReactionEntity save(QuestionReactionEntity reactionEntity);

    QuestionReactionEntity findByUserAndQuestion(UserEntity user, QuestionEntity question);

    Long countByQuestionIdAndReactionType(Long questionId, ReactionType reactionType);

    void deleteByUserAndQuestion(UserEntity user, QuestionEntity question);
    void deleteAllByQuestion(QuestionEntity question);
    void delete(QuestionReactionEntity reactionEntity);
}
