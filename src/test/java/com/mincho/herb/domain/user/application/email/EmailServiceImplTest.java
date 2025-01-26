package com.mincho.herb.domain.user.application.email;

import com.mincho.herb.common.util.ValidationUtils;
import com.mincho.herb.domain.user.dto.RequestVerification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private ValidationUtils validationUtils;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private EmailServiceImpl emailService;

    private static final String SENDER_EMAIL = "sender@example.com";
    private static final String TEST_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailServiceImpl(redisTemplate, javaMailSender, validationUtils, SENDER_EMAIL);
    }

    @Test
    void sendVerificationCode_ShouldStoreCode_inRedisAndSendEmail() throws MessagingException {
        // Given
        String generatedCode = "12345";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(validationUtils.createAuthCode(5)).thenReturn(generatedCode);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendVerificationCode(TEST_EMAIL);

        // Then
        verify(javaMailSender).send(mimeMessage); // 이메일이 발송되었는지 확인
        verify(redisTemplate).opsForValue().set(eq(TEST_EMAIL), eq(generatedCode), eq(300L), eq(TimeUnit.SECONDS)); // Redis에 코드가 저장되었는지 확인
    }

    @Test
    void emailVerification_ShouldReturnTrue_whenCodeMatches() {
        // Given
        String authCode = "12345";
        RequestVerification request = new RequestVerification(TEST_EMAIL, authCode);
        when(redisTemplate.opsForValue().get(TEST_EMAIL)).thenReturn(authCode);

        // When
        boolean result = emailService.emailVerification(request);

        // Then
        assertTrue(result); // 인증 성공 여부 확인
        verify(redisTemplate).delete(TEST_EMAIL); // 인증 성공 시 Redis에서 키 삭제
    }

    @Test
    void emailVerification_ShouldReturnFalse_whenCodeDoesNotMatch() {
        // Given
        String authCode = "12345";
        RequestVerification request = new RequestVerification(TEST_EMAIL, "wrongCode");
        when(redisTemplate.opsForValue().get(TEST_EMAIL)).thenReturn(authCode);

        // When
        boolean result = emailService.emailVerification(request);

        // Then
        assertFalse(result); // 인증 실패 여부 확인
        verify(redisTemplate, never()).delete(TEST_EMAIL); // 인증 실패 시 Redis에서 키 삭제 안 됨
    }

    @Test
    void createMail_ShouldReturnValidMimeMessage() throws MessagingException {
        // Given
        String authCode = "12345";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        MimeMessage message = emailService.createMail(TEST_EMAIL, authCode);

        // Then
        assertNotNull(message); // 이메일 객체가 생성되었는지 확인
    }
}