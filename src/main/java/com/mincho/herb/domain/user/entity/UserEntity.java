package com.mincho.herb.domain.user.entity;

import com.mincho.herb.domain.user.domain.User;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Member")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;
    private String role;

    private String provider; // 리소스 서버 제공자
    private String providerId; // 리소스 서버 식별자

    @OneToOne(mappedBy = "user")
    private ProfileEntity profile;

    private LocalDateTime lastLoginAt; // 마지막 로그인 시간




    // 엔티티로
    public static UserEntity toEntity(User UserDomain){
        UserEntity userEntity = new UserEntity();
        userEntity.id = UserDomain.getId();
        userEntity.email = UserDomain.getEmail();
        userEntity.password = UserDomain.getPassword();
        userEntity.providerId = UserDomain.getProviderId();
        userEntity.provider  =UserDomain.getProvider();
        userEntity.role = UserDomain.getRole();
        userEntity.lastLoginAt = UserDomain.getLastLoginAt();


        return userEntity;
    }

    // 도메인으로
    public User toModel(){
        return User.builder()
                .id(this.id)
                .email(this.email)
                .password(this.password)
                .provider(this.provider)
                .providerId(this.providerId)
                .role(this.role)
                .lastLoginAt(this.lastLoginAt)
                .createdAt(this.getCreatedAt())
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

}
