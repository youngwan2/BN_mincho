package com.mincho.herb.domain.qna.repository.questionReaction;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity.ReactionType;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface QuestionReactionJpaRepository extends JpaRepository<QuestionReactionEntity, Long> {

    Optional<QuestionReactionEntity> findByUserAndQuestion(UserEntity user, QuestionEntity question);

    @Query("SELECT COUNT(r) FROM QuestionReactionEntity r WHERE r.question.id = :questionId AND r.reactionType = :reactionType")
    Long countByQuestionIdAndReactionType(@Param("questionId") Long questionId, @Param("reactionType") ReactionType reactionType);

    void deleteByUserAndQuestion(UserEntity user, QuestionEntity question);

    @Modifying
    @Query("DELETE FROM QuestionReactionEntity r WHERE r.question = :question")
    void deleteAllByQuestion(QuestionEntity question);
}
