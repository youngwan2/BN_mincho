package com.mincho.herb.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestVerification {

    @NotEmpty(message = "이메일은 필수 입니다.")
    private String email;
    @Size(min = 5, message = "코드는 5자리가 필수 입니다.")
    private String code;

}
