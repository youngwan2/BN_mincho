package com.mincho.herb.domain.user.entity;

import com.mincho.herb.domain.user.domain.RefreshToken;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "RefreshToken")
@Getter
@Setter
@RequiredArgsConstructor
public class RefreshTokenEntity extends BaseEntity {

    @Id
    private String refreshToken;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private UserEntity user;

    public static RefreshTokenEntity toEntity(String refreshToken, UserEntity user){
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.refreshToken = refreshToken;
        refreshTokenEntity.user = user;

        return refreshTokenEntity;

    }

    public RefreshToken toModel(){
        return RefreshToken.builder()
                .refreshToken(this.refreshToken)
                .build();
    }
}
