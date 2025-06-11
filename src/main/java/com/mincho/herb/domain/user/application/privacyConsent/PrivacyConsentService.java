package com.mincho.herb.domain.user.application.privacyConsent;

import com.mincho.herb.domain.user.dto.PrivacyConsentRequestDTO;
import com.mincho.herb.domain.user.dto.PrivacyConsentResponseDTO;

/**
 * 개인정보 수집 동의 서비스 인터페이스
 */
public interface PrivacyConsentService {

    /**
     * 개인정보 수집 동의 저장
     */
    PrivacyConsentResponseDTO saveConsent(PrivacyConsentRequestDTO requestDTO, String userEmail, String clientIp);

    /**
     * 현재 사용자의 동의 정보 조회
     */
    PrivacyConsentResponseDTO getCurrentUserConsent(String userEmail);

    /**
     * 개인정보 수집 동의 업데이트
     */
    PrivacyConsentResponseDTO updateConsent(PrivacyConsentRequestDTO requestDTO, String userEmail, String clientIp);

    /**
     * 마케팅 동의 여부 확인
     */
    boolean hasMarketingConsent(String userEmail);
}
