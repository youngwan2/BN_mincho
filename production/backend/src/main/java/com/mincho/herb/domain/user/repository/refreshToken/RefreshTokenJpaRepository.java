package com.mincho.herb.domain.user.repository.refreshToken;

import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    RefreshToken findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteAllByMemberId(Long id);
}
