package com.mincho.herb.global.aop.userActivity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityJpaRepository  extends JpaRepository<UserActivityLogEntity, Long> {
}
