package com.mincho.herb.domain.qna.repository.answerReaction;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity.ReactionType;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnswerReactionJpaRepository extends JpaRepository<AnswerReactionEntity, Long> {

    Optional<AnswerReactionEntity> findByUserAndAnswer(UserEntity user, AnswerEntity answer);

    @Query("SELECT COUNT(r) FROM AnswerReactionEntity r WHERE r.answer.id = :answerId AND r.reactionType = :reactionType")
    Long countByAnswerIdAndReactionType(@Param("answerId") Long answerId, @Param("reactionType") ReactionType reactionType);

    void deleteByUserAndAnswer(UserEntity user, AnswerEntity answer);
}
