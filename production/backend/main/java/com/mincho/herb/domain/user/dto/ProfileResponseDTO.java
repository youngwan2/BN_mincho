package com.mincho.herb.domain.user.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponseDTO {
    private String nickname;
    private String introduction;
    private String avatarUrl;
}
