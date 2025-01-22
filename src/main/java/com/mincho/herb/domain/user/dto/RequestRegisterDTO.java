package com.mincho.herb.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestRegisterDTO {

    @NotEmpty(message = "이메일 입력은 필수 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    @NotEmpty(message = "닉네임 입력은 필수 입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 최소 2자 이상 10자 이하이어야 합니다.")
    private String nickname;

    // 특수문자 1개 이상 포함 8자 이상 15자 이하
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호 형식과 일치하지 않습니다.")
    private String password;


    @Override
    public String toString() {
        return "RequestRegisterDTO{" +
                "email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
