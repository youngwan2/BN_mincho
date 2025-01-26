package com.mincho.herb.domain.user.repository.refreshToken;


import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.domain.User;

public interface RefreshTokenRepository {
    void saveRefreshToken(String refreshToken, User user);
    RefreshToken findByRefreshToken(String refreshToken);
    void removeRefreshToken(String refreshToken);
    void removeRefreshTokenAllByUserId(Long id);
}
