package com.mincho.herb.domain.user.application.privacyConsent;

import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.PrivacyConsent;
import com.mincho.herb.domain.user.dto.PrivacyConsentRequestDTO;
import com.mincho.herb.domain.user.dto.PrivacyConsentResponseDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.privacyConsent.PrivacyConsentRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 개인정보 수집 동의 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PrivacyConsentServiceImpl implements PrivacyConsentService {

    private final PrivacyConsentRepository privacyConsentRepository;
    private final UserService userService;

    /**
     * 개인정보 수집 동의 저장
     */
    @Override
    @Transactional
    public PrivacyConsentResponseDTO saveConsent(PrivacyConsentRequestDTO requestDTO, String userEmail, String clientIp) {
        // 필수 동의 여부 확인
        if (requestDTO.getEssentialInfoConsent() == null || !requestDTO.getEssentialInfoConsent()) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "필수 정보 수집에 대한 동의는 필수입니다.");
        }

        UserEntity user = userService.getUserByEmail(userEmail);

        // 기존 동의 정보 확인 - 있으면 업데이트, 없으면 새로 생성
        PrivacyConsent existingConsent = privacyConsentRepository.findLatestByUser(user);
        if (existingConsent != null) {
            return updateConsent(requestDTO, userEmail, clientIp);
        }

        // 동의 정보 생성
        PrivacyConsent privacyConsent = PrivacyConsent.builder()
                .essentialInfoConsent(requestDTO.getEssentialInfoConsent())
                .optionalInfoConsent(requestDTO.getOptionalInfoConsent() != null ? requestDTO.getOptionalInfoConsent() : false)
                .automaticInfoConsent(requestDTO.getAutomaticInfoConsent() != null ? requestDTO.getAutomaticInfoConsent() : false)
                .marketingConsent(requestDTO.getMarketingConsent() != null ? requestDTO.getMarketingConsent() : false)
                .consentIp(clientIp)
                .build();

        PrivacyConsent savedConsent = privacyConsentRepository.save(privacyConsent, user);
        return PrivacyConsentResponseDTO.fromDomain(savedConsent);
    }

    /**
     * 현재 사용자의 동의 정보 조회
     */
    @Override
    @Transactional(readOnly = true)
    public PrivacyConsentResponseDTO getCurrentUserConsent(String userEmail) {
        UserEntity user = userService.getUserByEmail(userEmail);
        PrivacyConsent consent = privacyConsentRepository.findLatestByUser(user);

        if (consent == null) {
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "동의 정보가 없습니다.");
        }

        return PrivacyConsentResponseDTO.fromDomain(consent);
    }

    /**
     * 개인정보 수집 동의 업데이트
     */
    @Override
    @Transactional
    public PrivacyConsentResponseDTO updateConsent(PrivacyConsentRequestDTO requestDTO, String userEmail, String clientIp) {
        // 필수 동의 여부 확인
        if (requestDTO.getEssentialInfoConsent() == null || !requestDTO.getEssentialInfoConsent()) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "필수 정보 수집에 대한 동의는 필수입니다.");
        }

        UserEntity user = userService.getUserByEmail(userEmail);
        PrivacyConsent latestConsent = privacyConsentRepository.findLatestByUser(user);

        if (latestConsent == null) {
            // 기존 동의가 없으면 새로 생성
            return saveConsent(requestDTO, userEmail, clientIp);
        }

        // 업데이트할 동의 정보 생성
        PrivacyConsent updatedConsent = PrivacyConsent.builder()
                .id(latestConsent.getId())
                .userId(user.getId())
                .essentialInfoConsent(requestDTO.getEssentialInfoConsent())
                .optionalInfoConsent(requestDTO.getOptionalInfoConsent() != null ? requestDTO.getOptionalInfoConsent() : latestConsent.getOptionalInfoConsent())
                .automaticInfoConsent(requestDTO.getAutomaticInfoConsent() != null ? requestDTO.getAutomaticInfoConsent() : latestConsent.getAutomaticInfoConsent())
                .marketingConsent(requestDTO.getMarketingConsent() != null ? requestDTO.getMarketingConsent() : latestConsent.getMarketingConsent())
                .consentIp(clientIp)
                .build();

        PrivacyConsent result = privacyConsentRepository.update(latestConsent.getId(), updatedConsent);
        return PrivacyConsentResponseDTO.fromDomain(result);
    }

    /**
     * 마케팅 동의 여부 확인
     */
    @Override
    @Transactional(readOnly = true)
    public boolean hasMarketingConsent(String userEmail) {
        UserEntity user = userService.getUserByEmail(userEmail);
        return privacyConsentRepository.hasMarketingConsent(user);
    }
}
