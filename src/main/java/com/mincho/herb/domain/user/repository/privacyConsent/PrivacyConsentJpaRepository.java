package com.mincho.herb.domain.user.repository.privacyConsent;

import com.mincho.herb.domain.user.entity.PrivacyConsentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 개인정보 수집 동의 JPA 리포지토리
 */
public interface PrivacyConsentJpaRepository extends JpaRepository<PrivacyConsentEntity, Long> {

    /**
     * 사용자 ID로 최신 동의 정보 조회
     */
    Optional<PrivacyConsentEntity> findFirstByUserOrderByConsentDateTimeDesc(UserEntity user);

    /**
     * 사용자 ID로 마케팅 동의 여부 조회
     */
    boolean existsByUserAndMarketingConsentTrue(UserEntity user);


    /**
     * 사용자 ID로 동의 정보 존재 확인
     */
    boolean existsByUser(UserEntity user);

    /**
     * 사용자 ID로 동의정보 제거
     */
    void deleteByUser(UserEntity user);
}
