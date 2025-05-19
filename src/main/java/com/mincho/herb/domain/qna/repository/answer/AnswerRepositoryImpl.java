package com.mincho.herb.domain.qna.repository.answer;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {
}
