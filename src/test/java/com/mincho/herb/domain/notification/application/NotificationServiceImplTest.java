package com.mincho.herb.domain.notification.application;

import com.mincho.herb.domain.notification.domain.Notification;
import com.mincho.herb.domain.notification.dto.NotificationReadStateResponseDTO;
import com.mincho.herb.domain.notification.dto.NotificationsResponse;
import com.mincho.herb.domain.notification.entity.NotificationEntity;
import com.mincho.herb.domain.notification.repository.NotificationRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * NotificationServiceImpl 클래스의 유닛 테스트 클래스입니다.
 * <p>
 * 사용자 알림 관련 서비스 로직에 대해 단위 테스트를 수행합니다.
 * Mockito를 사용하여 외부 의존성을 Mock 처리하고 JUnit 5 기반으로 테스트를 구성합니다.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private final String mockEmail = "user@example.com";


    /**
     * 알림 저장 로직에 대해 저장된 엔티티가 반환되는지 검증합니다.
     */
    @Test
    void save_ShouldPersistNotificationSuccessfully() {
        Long userId = 1L;
        String type = "INFO";
        String path = "/path";
        String message = "Test message";

        NotificationEntity mockEntity = new NotificationEntity();
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(mockEntity);

        NotificationEntity result = notificationService.save(userId, type, path, message);

        assertNotNull(result);
        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }

    /**
     * 사용자의 알림이 존재하지 않을 경우, 빈 응답을 반환하는지 검증합니다.
     */
    @Test
    void getNotifications_ShouldReturnEmptyResponseForNoNotifications() {
        setUpMockAuthentication(); // Mock Authentication
        int page = 0;
        int size = 10;
        String email = "user@example.com";

        when(userService.getUserByEmail(email)).thenReturn(new UserEntity(1L, null, null, null, null, null, null, null));
        when(notificationRepository.findAllByUserId(anyLong(), any(Pageable.class))).thenReturn(List.of());
        when(notificationRepository.countByUserId(anyLong())).thenReturn(0L);

        NotificationsResponse response = notificationService.getNotifications(page, size);

        assertNotNull(response);
        assertTrue(response.getNotifications().isEmpty());
        assertEquals(0, response.getTotalCount());
    }

    /**
     * SseEmitter를 통해 알림을 전송하는 로직이 정상적으로 수행되는지 검증합니다.
     */
    @Test
    void sendNotification_ShouldSendEventToEmitter() throws IOException {
        Long userId = 1L;
        String type = "INFO";
        String path = "/path";
        String message = "Test message";

        SseEmitter emitter = mock(SseEmitter.class);
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(new NotificationEntity());

        notificationService.addEmitterForTest(userId, emitter); // 테스트 전용 메서드 활용
        notificationService.sendNotification(userId, type, path, message);

        verify(emitter, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }

    /**
     * 알림을 읽음 처리하는 로직이 정상적으로 수행되는지 검증합니다.
     * - NotificationEntity → 도메인 모델 변환
     * - 도메인 모델의 읽음 처리
     * - 다시 Entity로 저장
     */
    @Test
    void markAsRead_ShouldUpdateNotificationAsRead() {
        Long notificationId = 1L;

        // Mock NotificationEntity
        NotificationEntity mockEntity = mock(NotificationEntity.class);

        // Mock Notification (도메인 모델)
        Notification mockDomainModel = mock(Notification.class);

        // stubbing
        when(notificationRepository.findById(notificationId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.toModel()).thenReturn(mockDomainModel);
        when(notificationRepository.save(any(NotificationEntity.class))).thenReturn(mockEntity);

        // 실행
        notificationService.markAyRead(notificationId);

        // 검증
        verify(mockEntity, times(1)).toModel();
        verify(mockDomainModel, times(1)).markAsRead();
        verify(notificationRepository, times(1)).save(any(NotificationEntity.class));
    }

    /**
     * 사용자가 읽지 않은 알림이 하나라도 존재하면 false를 반환하는지 검증합니다.
     */
    @Test
    void getNotificationReadState_ShouldReturnCorrectState() {
        setUpMockAuthentication(); // Mock Authentication


        UserEntity user = new UserEntity(1L, null, null, null, null, null, null, null);

        when(userService.getUserByEmail(mockEmail)).thenReturn(user);
        when(notificationRepository.existsByUserIdAndIsReadFalse(anyLong())).thenReturn(false);

        NotificationReadStateResponseDTO response = notificationService.getNotificationReadState();

        assertNotNull(response);
        assertFalse(response.getIsAllRead());
    }


    /**
     * 테스트를 위한 SecurityContext 설정 메서드입니다.
     * <p>
     * SecurityContextHolder에 Mock된 Authentication 객체를 설정하여
     * 인증 정보를 사용하는 서비스 로직의 테스트가 가능하게 합니다.
     * </p>
     */
    private void setUpMockAuthentication() {
        // 1. Authentication Mock
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(mockEmail);

        // 2. SecurityContext Mock
        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        // 3. SecurityContextHolder 에 최종 설정
        SecurityContextHolder.setContext(context);

    }
}
