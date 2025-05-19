package com.mincho.herb.domain.qna.repository.answer;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, Long> {

    Optional<AnswerEntity> findByQnaId(Long qnaId);
}
