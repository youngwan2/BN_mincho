package com.mincho.herb.domain.user.entity;

import com.mincho.herb.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "Member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEntity {

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

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatusEnum status = UserStatusEnum.ACTIVE; // 계정 활성화 여부

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;



    // 엔티티로
    public static UserEntity toEntity(User userDomain){
        UserEntity userEntity = new UserEntity();
        userEntity.id = userDomain.getId();
        userEntity.email = userDomain.getEmail();
        userEntity.password = userDomain.getPassword();
        userEntity.providerId = userDomain.getProviderId();
        userEntity.provider  =userDomain.getProvider();
        userEntity.role = userDomain.getRole();
        userEntity.lastLoginAt = userDomain.getLastLoginAt();
        userEntity.status = userDomain.getStatus();

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
                .createdAt(this.createdAt)

                .status(this.status)
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

}
