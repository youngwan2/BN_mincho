package com.mincho.herb.domain.user.dto;


import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UpdatePasswordRequestDTO {
    // 특수문자 1개 이상 포함 8자 이상 15자 이하
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호 형식과 일치하지 않습니다.")
    private String password;

}
