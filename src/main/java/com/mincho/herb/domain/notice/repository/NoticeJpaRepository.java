package com.mincho.herb.domain.notice.repository;

import com.mincho.herb.domain.notice.entity.NoticeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<NoticeEntity, Long> {
}
