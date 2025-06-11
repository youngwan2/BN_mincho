package com.mincho.herb.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 개인정보 수집 및 마케팅 정보 동의에 대한 도메인 모델
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrivacyConsent {
    private Long id;
    private Long userId;
    private Boolean essentialInfoConsent; // 필수 정보 수집 동의 여부 (이메일, 비밀번호, 닉네임 등)
    private Boolean optionalInfoConsent; // 선택 정보 수집 동의 여부 (성별, 연령대, 주요 증상 등)
    private Boolean automaticInfoConsent; // 자동 수집 정보 동의 여부 (기기 정보, 브라우저 정보 등)
    private Boolean marketingConsent; // 마케팅 정보 수집 동의 여부
    private LocalDateTime consentDateTime; // 동의 일시
    private String consentIp; // 동의 IP
}
