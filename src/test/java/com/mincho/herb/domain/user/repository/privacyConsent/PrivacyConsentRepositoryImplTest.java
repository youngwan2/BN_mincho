package com.mincho.herb.domain.user.repository.privacyConsent;

import com.mincho.herb.domain.user.domain.PrivacyConsent;
import com.mincho.herb.domain.user.entity.PrivacyConsentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.user.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(PrivacyConsentRepositoryImpl.class)
class PrivacyConsentRepositoryImplTest {

    @Autowired
    private PrivacyConsentRepository privacyConsentRepository;

    @Autowired
    private PrivacyConsentJpaRepository privacyConsentJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        // 테스트용 사용자 생성
        testUser = UserEntity.builder()
                .email("test@example.com")
                .password("password")
                .build();

        userJpaRepository.save(testUser);
    }

    @Test
    @DisplayName("개인정보 수집 동의 저장 테스트")
    void savePrivacyConsentTest() {
        // Given
        PrivacyConsent privacyConsent = PrivacyConsent.builder()
                .essentialInfoConsent(true)
                .optionalInfoConsent(false)
                .automaticInfoConsent(true)
                .marketingConsent(false)
                .consentIp("127.0.0.1")
                .build();

        // When
        PrivacyConsent savedConsent = privacyConsentRepository.save(privacyConsent, testUser);

        // Then
        assertNotNull(savedConsent);
        assertNotNull(savedConsent.getId());
        assertEquals(testUser.getId(), savedConsent.getUserId());
        assertTrue(savedConsent.getEssentialInfoConsent());
        assertFalse(savedConsent.getOptionalInfoConsent());
        assertTrue(savedConsent.getAutomaticInfoConsent());
        assertFalse(savedConsent.getMarketingConsent());
        assertEquals("127.0.0.1", savedConsent.getConsentIp());
        assertNotNull(savedConsent.getConsentDateTime());
    }

    @Test
    @DisplayName("사용자 ID로 최신 동의 정보 조회")
    void findLatestByUserTest() {
        // Given
        // 첫 번째 동의 정보
        PrivacyConsentEntity firstConsent = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(false)
                .automaticInfoConsent(true)
                .marketingConsent(false)
                .consentIp("127.0.0.1")
                .build();
        privacyConsentJpaRepository.save(firstConsent);

        // 잠시 대기
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 두 번째 동의 정보 (최신)
        PrivacyConsentEntity secondConsent = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)  // 변경됨
                .automaticInfoConsent(true)
                .marketingConsent(true)     // 변경됨
                .consentIp("192.168.0.1")   // 변경됨
                .build();
        privacyConsentJpaRepository.save(secondConsent);

        // When
        PrivacyConsent latestConsent = privacyConsentRepository.findLatestByUser(testUser);

        // Then
        assertNotNull(latestConsent);
        assertEquals(secondConsent.getId(), latestConsent.getId());
        assertTrue(latestConsent.getOptionalInfoConsent());
        assertTrue(latestConsent.getMarketingConsent());
        assertEquals("192.168.0.1", latestConsent.getConsentIp());
    }

    @Test
    @DisplayName("동의 ID로 동의 정보 조회")
    void findByIdTest() {
        // Given
        PrivacyConsentEntity consentEntity = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(true)
                .consentIp("127.0.0.1")
                .build();
        PrivacyConsentEntity savedEntity = privacyConsentJpaRepository.save(consentEntity);

        // When
        PrivacyConsent foundConsent = privacyConsentRepository.findById(savedEntity.getId());

        // Then
        assertNotNull(foundConsent);
        assertEquals(savedEntity.getId(), foundConsent.getId());
        assertEquals(testUser.getId(), foundConsent.getUserId());
        assertTrue(foundConsent.getEssentialInfoConsent());
        assertTrue(foundConsent.getOptionalInfoConsent());
        assertTrue(foundConsent.getAutomaticInfoConsent());
        assertTrue(foundConsent.getMarketingConsent());
    }

    @Test
    @DisplayName("사용자의 마케팅 동의 여부 확인")
    void hasMarketingConsentTest() {
        // Given
        // 마케팅 미동의 사용자
        PrivacyConsentEntity noMarketingConsent = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(false)
                .consentIp("127.0.0.1")
                .build();
        privacyConsentJpaRepository.save(noMarketingConsent);

        // When & Then
        assertFalse(privacyConsentRepository.hasMarketingConsent(testUser));

        // 마케팅 동의 추가
        PrivacyConsentEntity withMarketingConsent = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(true)
                .consentIp("127.0.0.1")
                .build();
        privacyConsentJpaRepository.save(withMarketingConsent);

        // When & Then
        assertTrue(privacyConsentRepository.hasMarketingConsent(testUser));
    }

    @Test
    @DisplayName("동의 정보 업데이트")
    void updateTest() {
        // Given
        PrivacyConsentEntity initialConsent = PrivacyConsentEntity.builder()
                .user(testUser)
                .essentialInfoConsent(true)
                .optionalInfoConsent(false)
                .automaticInfoConsent(true)
                .marketingConsent(false)
                .consentIp("127.0.0.1")
                .build();
        PrivacyConsentEntity savedEntity = privacyConsentJpaRepository.save(initialConsent);

        // 업데이트할 동의 정보
        PrivacyConsent updatedConsent = PrivacyConsent.builder()
                .id(savedEntity.getId())
                .userId(testUser.getId())
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)  // 변경
                .automaticInfoConsent(true)
                .marketingConsent(true)     // 변경
                .consentIp("192.168.0.1")   // 변경
                .build();

        // When
        PrivacyConsent result = privacyConsentRepository.update(savedEntity.getId(), updatedConsent);

        // Then
        assertNotNull(result);
        assertEquals(savedEntity.getId(), result.getId());
        assertTrue(result.getOptionalInfoConsent());
        assertTrue(result.getMarketingConsent());
        assertEquals("192.168.0.1", result.getConsentIp());

        // DB에서 직접 조회해서 확인
        PrivacyConsentEntity updatedEntity = privacyConsentJpaRepository.findById(savedEntity.getId()).orElse(null);
        assertNotNull(updatedEntity);
        assertTrue(updatedEntity.getOptionalInfoConsent());
        assertTrue(updatedEntity.getMarketingConsent());
        assertEquals("192.168.0.1", updatedEntity.getConsentIp());
    }
}
