package com.mincho.herb.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserListDTO {
    private Long id;
    private String nickname;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private String isSocial;
}
