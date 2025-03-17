package com.mincho.herb.domain.user.domain;


import com.mincho.herb.domain.user.dto.ProfileRequestDTO;
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

    public static Profile withChangeProfile(ProfileRequestDTO profileRequestDTO){
        return Profile.builder()
                .nickname(profileRequestDTO.getNickname())
                .avatarUrl(profileRequestDTO.getAvatarUrl())
                .introduction(profileRequestDTO.getIntroduction())
                .build();
    }
}
