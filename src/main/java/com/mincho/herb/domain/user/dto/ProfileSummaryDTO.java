package com.mincho.herb.domain.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSummaryDTO {
    private String nickname;
    private String introduction;
    private String avatarUrl;
}
