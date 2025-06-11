package com.mincho.herb.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개인정보 수집 동의 요청 DTO")
public class PrivacyConsentRequestDTO {

    @NotNull(message = "필수 정보 수집 동의 여부는 필수입니다.")
    @Schema(description = "필수 정보 수집 동의 여부 (이메일, 비밀번호, 닉네임 등)", required = true)
    private Boolean essentialInfoConsent;

    @Schema(description = "선택 정보 수집 동의 여부 (성별, 연령대, 주요 증상 등)")
    private Boolean optionalInfoConsent;

    @Schema(description = "자동 수집 정보 동의 여부 (기기 정보, 브라우저 정보 등)")
    private Boolean automaticInfoConsent;

    @Schema(description = "마케팅 정보 수집 동의 여부")
    private Boolean marketingConsent;
}
