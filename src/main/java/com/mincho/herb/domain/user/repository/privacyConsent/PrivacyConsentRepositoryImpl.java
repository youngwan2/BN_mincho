package com.mincho.herb.domain.user.repository.privacyConsent;

import com.mincho.herb.domain.user.domain.PrivacyConsent;
import com.mincho.herb.domain.user.entity.PrivacyConsentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 개인정보 수집 동의 리포지토리 구현체
 */
@Repository
@RequiredArgsConstructor
public class PrivacyConsentRepositoryImpl implements PrivacyConsentRepository {

    private final PrivacyConsentJpaRepository privacyConsentJpaRepository;

    /**
     * 개인정보 수집 동의 저장
     */
    @Override
    public PrivacyConsent save(PrivacyConsent privacyConsent, UserEntity user) {
        PrivacyConsentEntity entity = PrivacyConsentEntity.fromDomain(privacyConsent, user);
        PrivacyConsentEntity savedEntity = privacyConsentJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    /**
     * 사용자 ID로 최신 동의 정보 조회
     */
    @Override
    public PrivacyConsent findLatestByUser(UserEntity user) {
        return privacyConsentJpaRepository.findFirstByUserOrderByConsentDateTimeDesc(user)
                .map(PrivacyConsentEntity::toDomain)
                .orElse(null);
    }

    /**
     * 동의 ID로 동의 정보 조회
     */
    @Override
    public PrivacyConsent findById(Long id) {
        return privacyConsentJpaRepository.findById(id)
                .map(PrivacyConsentEntity::toDomain)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 동의 정보를 찾을 수 없습니다."));
    }

    /**
     * 사용자의 마케팅 동의 여부 확인
     */
    @Override
    public boolean hasMarketingConsent(UserEntity user) {
        return privacyConsentJpaRepository.existsByUserAndMarketingConsentTrue(user);
    }

    /**
     * 동의 정보 업데이트
     */
    @Override
    public PrivacyConsent update(Long id, PrivacyConsent privacyConsent) {
        PrivacyConsentEntity entity = privacyConsentJpaRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 동의 정보를 찾을 수 없습니다."));

        entity.updateConsent(
                privacyConsent.getEssentialInfoConsent(),
                privacyConsent.getOptionalInfoConsent(),
                privacyConsent.getAutomaticInfoConsent(),
                privacyConsent.getMarketingConsent(),
                privacyConsent.getConsentIp()
        );

        PrivacyConsentEntity updatedEntity = privacyConsentJpaRepository.save(entity);
        return updatedEntity.toDomain();
    }

    /**
     * 동의 정보 삭제
     *
     * @param user
     */
    @Override
    public void deleteByUser(UserEntity user) {
        if (!privacyConsentJpaRepository.existsByUser(user)) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 유저의 개인정보 동의 정보를 찾을 수 없습니다.");
        }
        privacyConsentJpaRepository.deleteByUser(user);

    }
}
