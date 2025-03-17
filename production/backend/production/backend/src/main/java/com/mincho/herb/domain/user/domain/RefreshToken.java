package com.mincho.herb.domain.user.domain;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class RefreshToken {
    private String refreshToken;
}
