package com.mincho.herb.domain.user.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateCheckDTO {

    @NotEmpty(message = "이메일 입력은 필수 입니다.")
    String email;
}
