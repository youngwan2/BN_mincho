package com.mincho.herb.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotEmpty(message = "이메일 입력은 필수 입니다.")
    String email;

    @NotEmpty(message = "비밀번호 입력은 필수 입니다.")
    String password;

}
