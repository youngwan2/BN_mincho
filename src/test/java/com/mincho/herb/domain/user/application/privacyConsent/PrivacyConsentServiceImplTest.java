package com.mincho.herb.domain.user.application.privacyConsent;

import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.domain.PrivacyConsent;
import com.mincho.herb.domain.user.dto.PrivacyConsentRequestDTO;
import com.mincho.herb.domain.user.dto.PrivacyConsentResponseDTO;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.domain.user.repository.privacyConsent.PrivacyConsentRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrivacyConsentServiceImplTest {

    @Mock
    private PrivacyConsentRepository privacyConsentRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private PrivacyConsentServiceImpl privacyConsentService;

    private UserEntity testUser;
    private String testEmail;
    private String testIp;
    private PrivacyConsentRequestDTO validRequestDTO;
    private PrivacyConsent testPrivacyConsent;

    @BeforeEach
    void setUp() {
        testEmail = "test@example.com";
        testIp = "127.0.0.1";

        // 테스트용 사용자
        testUser = UserEntity.builder()
                .id(1L)
                .email(testEmail)
                .build();

        // 유효한 요청 DTO
        validRequestDTO = PrivacyConsentRequestDTO.builder()
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(true)
                .build();

        // 테스트용 동의 도메인 객체
        testPrivacyConsent = PrivacyConsent.builder()
                .id(1L)
                .userId(testUser.getId())
                .essentialInfoConsent(true)
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(true)
                .consentIp(testIp)
                .consentDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("새 개인정보 수집 동의 저장 성공")
    void saveConsentSuccessTest() {
        // Given
        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.findLatestByUser(testUser)).thenReturn(null);
        when(privacyConsentRepository.save(any(PrivacyConsent.class), eq(testUser))).thenReturn(testPrivacyConsent);

        // When
        PrivacyConsentResponseDTO responseDTO = privacyConsentService.saveConsent(validRequestDTO, testEmail, testIp);

        // Then
        assertNotNull(responseDTO);
        assertEquals(testPrivacyConsent.getId(), responseDTO.getId());
        assertEquals(testUser.getId(), responseDTO.getUserId());
        assertTrue(responseDTO.getEssentialInfoConsent());
        assertTrue(responseDTO.getOptionalInfoConsent());
        assertTrue(responseDTO.getAutomaticInfoConsent());
        assertTrue(responseDTO.getMarketingConsent());

        verify(privacyConsentRepository).findLatestByUser(testUser);
        verify(privacyConsentRepository).save(any(PrivacyConsent.class), eq(testUser));
    }

    @Test
    @DisplayName("필수 정보 동의 없이 저장 시 예외 발생")
    void saveConsentWithoutEssentialConsentTest() {
        // Given
        PrivacyConsentRequestDTO invalidRequest = PrivacyConsentRequestDTO.builder()
                .essentialInfoConsent(false) // 필수 동의 거부
                .optionalInfoConsent(true)
                .automaticInfoConsent(true)
                .marketingConsent(true)
                .build();

        // When & Then
        assertThrows(CustomHttpException.class, () ->
            privacyConsentService.saveConsent(invalidRequest, testEmail, testIp)
        );

        // 리포지토리 메소드 호출 안 됨
        verify(privacyConsentRepository, never()).save(any(), any());
    }

    @Test
    @DisplayName("기존 동의가 있는 경우 업데이트로 처리")
    void saveConsentWithExistingConsentTest() {
        // Given
        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.findLatestByUser(testUser)).thenReturn(testPrivacyConsent);
        when(privacyConsentRepository.update(eq(testPrivacyConsent.getId()), any(PrivacyConsent.class))).thenReturn(testPrivacyConsent);

        // When
        PrivacyConsentResponseDTO responseDTO = privacyConsentService.saveConsent(validRequestDTO, testEmail, testIp);

        // Then
        assertNotNull(responseDTO);
        // findLatestByUser가 호출되지만 정확한 횟수는 검증하지 않음 (구현에 따라 달라질 수 있음)
        verify(privacyConsentRepository, atLeastOnce()).findLatestByUser(testUser);
        verify(privacyConsentRepository).update(eq(testPrivacyConsent.getId()), any(PrivacyConsent.class));
        verify(privacyConsentRepository, never()).save(any(), any()); // save는 호출되면 안 됨
    }

    @Test
    @DisplayName("현재 사용자의 동의 정보 조회 성공")
    void getCurrentUserConsentTest() {
        // Given
        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.findLatestByUser(testUser)).thenReturn(testPrivacyConsent);

        // When
        PrivacyConsentResponseDTO responseDTO = privacyConsentService.getCurrentUserConsent(testEmail);

        // Then
        assertNotNull(responseDTO);
        assertEquals(testPrivacyConsent.getId(), responseDTO.getId());
        assertEquals(testUser.getId(), responseDTO.getUserId());
        verify(privacyConsentRepository).findLatestByUser(testUser);
    }

    @Test
    @DisplayName("동의 정보가 없는 경우 예외 발생")
    void getCurrentUserConsentWhenNotExistsTest() {
        // Given
        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.findLatestByUser(testUser)).thenReturn(null);

        // When & Then
        assertThrows(CustomHttpException.class, () ->
            privacyConsentService.getCurrentUserConsent(testEmail)
        );
        verify(privacyConsentRepository).findLatestByUser(testUser);
    }

    @Test
    @DisplayName("동의 정보 업데이�� 성공")
    void updateConsentTest() {
        // Given
        // 업데이트된 요청
        PrivacyConsentRequestDTO updateRequest = PrivacyConsentRequestDTO.builder()
                .essentialInfoConsent(true)
                .optionalInfoConsent(false)  // 변경
                .automaticInfoConsent(true)
                .marketingConsent(false)     // 변경
                .build();

        // 업데이트된 결과
        PrivacyConsent updatedConsent = PrivacyConsent.builder()
                .id(testPrivacyConsent.getId())
                .userId(testUser.getId())
                .essentialInfoConsent(true)
                .optionalInfoConsent(false)  // 변경됨
                .automaticInfoConsent(true)
                .marketingConsent(false)     // 변경됨
                .consentIp(testIp)
                .consentDateTime(LocalDateTime.now())
                .build();

        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.findLatestByUser(testUser)).thenReturn(testPrivacyConsent);
        when(privacyConsentRepository.update(eq(testPrivacyConsent.getId()), any(PrivacyConsent.class))).thenReturn(updatedConsent);

        // When
        PrivacyConsentResponseDTO responseDTO = privacyConsentService.updateConsent(updateRequest, testEmail, testIp);

        // Then
        assertNotNull(responseDTO);
        assertEquals(testPrivacyConsent.getId(), responseDTO.getId());
        assertEquals(testUser.getId(), responseDTO.getUserId());
        assertTrue(responseDTO.getEssentialInfoConsent());
        assertFalse(responseDTO.getOptionalInfoConsent()); // 변경 확인
        assertTrue(responseDTO.getAutomaticInfoConsent());
        assertFalse(responseDTO.getMarketingConsent()); // 변경 확인

        verify(privacyConsentRepository).findLatestByUser(testUser);
        verify(privacyConsentRepository).update(eq(testPrivacyConsent.getId()), any(PrivacyConsent.class));
    }

    @Test
    @DisplayName("마케팅 동의 여부 확인")
    void hasMarketingConsentTest() {
        // Given
        when(userService.getUserByEmail(testEmail)).thenReturn(testUser);
        when(privacyConsentRepository.hasMarketingConsent(testUser)).thenReturn(true);

        // When
        boolean result = privacyConsentService.hasMarketingConsent(testEmail);

        // Then
        assertTrue(result);
        verify(privacyConsentRepository).hasMarketingConsent(testUser);
    }
}
