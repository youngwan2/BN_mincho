package com.mincho.herb.domain.qna.repository.qna;

import com.mincho.herb.domain.qna.entity.QnaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaJpaRepository extends JpaRepository<QnaEntity, Long> {
}
