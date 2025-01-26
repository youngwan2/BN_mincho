package com.mincho.herb.domain.user.entity;
import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.user.domain.RefreshToken;
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
    private UserEntity member;

    public static RefreshTokenEntity toEntity(String refreshToken, UserEntity member){
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.refreshToken = refreshToken;
        refreshTokenEntity.member = member;

        return refreshTokenEntity;

    }

    public RefreshToken toModel(){
        return RefreshToken.builder()
                .refreshToken(this.refreshToken)
                .build();
    }
}
