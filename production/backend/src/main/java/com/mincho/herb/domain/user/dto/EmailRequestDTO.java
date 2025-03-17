package com.mincho.herb.domain.user.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {

    @NotEmpty(message = "이메일은 필수입니다.")
    private String email;
}
