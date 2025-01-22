package com.mincho.herb.domain.user.repository;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    void save(User user);
    boolean existsByEmail(String email);

}
