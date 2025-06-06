package com.mincho.herb.domain.qna.repository.question;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {
}
