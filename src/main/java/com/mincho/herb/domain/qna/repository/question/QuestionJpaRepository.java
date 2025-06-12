package com.mincho.herb.domain.qna.repository.question;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findAllByWriter(UserEntity writer);
}
