package com.mincho.herb.domain.user.domain;


import com.mincho.herb.domain.user.dto.RequestProfileDTO;
import lombok.*;

@Getter
@Setter
@Data
@Builder
public class Profile {
    private Long id;
    private String nickname;
    private String introduction;
    private String avatarUrl;

    public static Profile withChangeProfile(RequestProfileDTO requestProfileDTO){
        return Profile.builder()
                .nickname(requestProfileDTO.getNickname())
                .avatarUrl(requestProfileDTO.getAvatarUrl())
                .introduction(requestProfileDTO.getIntroduction())
                .build();
    }
}
