package com.mincho.herb.domain.user.repository.refreshToken;

import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.entity.RefreshTokenEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    RefreshToken findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteAllByUserId(Long id);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.user =:user")
    void deleteByUser(UserEntity user);
}
