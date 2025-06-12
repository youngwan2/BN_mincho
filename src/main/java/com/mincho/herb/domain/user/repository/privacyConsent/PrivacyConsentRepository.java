package com.mincho.herb.domain.user.repository.privacyConsent;

import com.mincho.herb.domain.user.domain.PrivacyConsent;
import com.mincho.herb.domain.user.entity.UserEntity;

/**
 * 개인정보 수집 동의 리포지토리 인터페이스
 */
public interface PrivacyConsentRepository {

    /**
     * 개인정보 수집 동의 저장
     */
    PrivacyConsent save(PrivacyConsent privacyConsent, UserEntity user);

    /**
     * 사용자 ID로 최신 동의 정보 조회
     */
    PrivacyConsent findLatestByUser(UserEntity user);

    /**
     * 동의 ID로 동의 정보 조회
     */
    PrivacyConsent findById(Long id);

    /**
     * 사용자의 마케팅 동의 여부 확인
     */
    boolean hasMarketingConsent(UserEntity user);

    /**
     * 동의 정보 업데이트
     */
    PrivacyConsent update(Long id, PrivacyConsent privacyConsent);

    /**
     * 동의 정보 삭제
     */
    void deleteByUser(UserEntity user);
}
