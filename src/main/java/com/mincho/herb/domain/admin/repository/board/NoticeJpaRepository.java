package com.mincho.herb.domain.admin.repository.board;

import com.mincho.herb.domain.admin.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {
}
