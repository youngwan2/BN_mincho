package com.mincho.herb.domain.user.dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ProfileRequestDTO {
    private String nickname;
    private String introduction;
    private String avatarUrl;
}
