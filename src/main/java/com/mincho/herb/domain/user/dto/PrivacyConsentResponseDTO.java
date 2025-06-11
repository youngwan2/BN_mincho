package com.mincho.herb.domain.user.dto;

import com.mincho.herb.domain.user.domain.PrivacyConsent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "개인정보 수집 동의 응답 DTO")
public class PrivacyConsentResponseDTO {

    @Schema(description = "동의 ID")
    private Long id;

    @Schema(description = "사용자 ID")
    private Long userId;

    @Schema(description = "필수 정보 수집 동의 여부")
    private Boolean essentialInfoConsent;

    @Schema(description = "선택 정보 수집 동의 여부")
    private Boolean optionalInfoConsent;

    @Schema(description = "자동 수집 정보 동의 여부")
    private Boolean automaticInfoConsent;

    @Schema(description = "마케팅 정보 수집 동의 여부")
    private Boolean marketingConsent;

    @Schema(description = "동의 일시")
    private LocalDateTime consentDateTime;

    /**
     * 도메인 객체로부터 DTO 생성
     */
    public static PrivacyConsentResponseDTO fromDomain(PrivacyConsent privacyConsent) {
        return PrivacyConsentResponseDTO.builder()
                .id(privacyConsent.getId())
                .userId(privacyConsent.getUserId())
                .essentialInfoConsent(privacyConsent.getEssentialInfoConsent())
                .optionalInfoConsent(privacyConsent.getOptionalInfoConsent())
                .automaticInfoConsent(privacyConsent.getAutomaticInfoConsent())
                .marketingConsent(privacyConsent.getMarketingConsent())
                .consentDateTime(privacyConsent.getConsentDateTime())
                .build();
    }
}
