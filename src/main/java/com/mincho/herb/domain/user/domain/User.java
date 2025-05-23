package com.mincho.herb.domain.user.domain;

import com.mincho.herb.domain.user.entity.UserStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String email;
    private String password;
    private String role;
    private Profile profile;
    private String provider;
    private String providerId;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private UserStatusEnum status; // 계정 활성화 여부
}
