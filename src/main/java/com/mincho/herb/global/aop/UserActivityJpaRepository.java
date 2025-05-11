package com.mincho.herb.global.aop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityJpaRepository  extends JpaRepository<UserActivityLogEntity, Long> {
}
