package com.mincho.herb.domain.user.dto;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileRequestDTO {
    @Size(min = 2, max = 12, message = "nickname은 최소 2자 이상 12자 까지 가능합니다.")
    private String nickname;

    @Size(max = 255, message = "introduction 은 255자 까지 가능합니다.")
    private String introduction;
    private String avatarUrl;
}
