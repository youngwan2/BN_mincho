package com.mincho.herb.domain.user.repository.refreshToken;


import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.entity.MemberEntity;

public interface RefreshTokenRepository {
    void saveRefreshToken(String refreshToken, MemberEntity memberEntity);
    RefreshToken findByRefreshToken(String refreshToken);
    void removeRefreshToken(String refreshToken);
    void removeRefreshTokenAllByUserId(Long id);
    void deleteByMember(MemberEntity member);
}
