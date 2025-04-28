package com.mincho.herb.domain.user.repository.refreshToken;

import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, Long> {

    RefreshToken findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteAllByMemberId(Long id);

    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rt WHERE rt.member =:member")
    void deleteByMember(MemberEntity member);
}
