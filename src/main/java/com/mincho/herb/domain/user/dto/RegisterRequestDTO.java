package com.mincho.herb.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotEmpty(message = "이메일 입력은 필수 입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String email;

    // 특수문자 1개 이상 포함 8자 이상 15자 이하
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,15}$", message = "비밀번호 형식과 일치하지 않습니다.")
    private String password;

    // 개인정보 수집 동의 관련 필드 추가
    private Boolean essentialInfoConsent; // 필수 정보 수집 동의 여부
    private Boolean optionalInfoConsent;  // 선택 정보 수집 동의 여부
    private Boolean automaticInfoConsent; // 자동 수집 정보 동의 여부
    private Boolean marketingConsent;     // 마케팅 정보 수집 동의 여부


}
