package com.mincho.herb.domain.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Member {
    private Long id;
    private String email;
    private String password;
    private String role;
    private Profile profile;
}
