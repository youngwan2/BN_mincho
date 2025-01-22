package com.mincho.herb.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {
    private Long id;
    private String nickname;
    private String email;
    private String password;
}
