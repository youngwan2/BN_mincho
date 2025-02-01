package com.mincho.herb.domain.user.repository.refreshToken;

import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.domain.user.entity.RefreshTokenEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository{

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void saveRefreshToken(String refreshToken, UserEntity userEntity) {
        refreshTokenJpaRepository.save(RefreshTokenEntity.toEntity(refreshToken,userEntity));
    }

    @Override
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenJpaRepository.findByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public void removeRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }

    @Override
    @Transactional
    public void removeRefreshTokenAllByUserId(Long id) {
        refreshTokenJpaRepository.deleteAllByMemberId(id);
    }
}
