package com.mincho.herb.domain.notification.application;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.notification.domain.Notification;
import com.mincho.herb.domain.notification.dto.NotificationDTO;
import com.mincho.herb.domain.notification.dto.NotificationsResponse;
import com.mincho.herb.domain.notification.entity.NotificationEntity;
import com.mincho.herb.domain.notification.repository.NotificationRepository;
import com.mincho.herb.domain.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.mincho.herb.domain.user.application.user.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl  implements NotificationService{
    
    private  final UserService userService;
    private final NotificationRepository notificationRepository;
    private final ConcurrentHashMap<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 알림 구독
    @Override
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"유효한 권한이 없습니다.");
        }

        Long userId =  userService.findUserByEmail(email).getId();

        emitterMap.put(userId, emitter);

        emitter.onCompletion(()-> emitterMap.remove(userId));
        emitter.onTimeout(()-> emitterMap.remove(userId));
        emitter.onError((e)-> emitterMap.remove(userId));

        try {
            // 최초 연결 직후 더미 데이터 보내기
            emitter.send(SseEmitter.event()
                    .name("connect") // 이벤트 이름 지정
                    .data("connected") // 메세지
                    .id(String.valueOf(userId))
            );
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        log.info("connections:{}", emitterMap);

        return emitter;
    }

    // 알림 저장
    @Override
    public NotificationEntity save(Long userId, String type,String path, String message) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .message(message)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .path(path)
                .build();
        
       return notificationRepository.save(NotificationEntity.toEntity(notification));
    }

    // 해당 사용자의 모든 알림 목록 조회
    @Override
    public NotificationsResponse getNotifications(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!email.contains("@")){
            return null;
        }

        Member member = userService.findUserByEmail(email);
        Long userId = member.getId();

        // 알림 목록
        List<NotificationEntity> notifications = notificationRepository.findAllByUserId(userId, pageable);

        if(notifications.isEmpty()) {
            notifications = List.of();
        }

        log.info("notifications:{}", notifications);
        List<NotificationDTO> notificationDTOS= notifications.stream().map(notificationEntity->{

            return NotificationDTO.builder()
                    .id(notificationEntity.getId())
                    .path(notificationEntity.getPath())
                    .type(notificationEntity.getType())
                    .isRead(notificationEntity.getIsRead())
                    .message(notificationEntity.getMessage())
                    .createdAt(notificationEntity.getCreatedAt())
                    .build();
        }).toList();

        // 알림 개수 통계
        Long totalCount = notificationRepository.countByUserId(userId);

        return NotificationsResponse.builder()
                .notifications(notificationDTOS)
                .totalCount(totalCount)
                .nextPage(page+1)
                .build();
    }

    // 알림 전송
    @Override
    public void sendNotification(Long userId, String type, String path, String message) {
        NotificationEntity savedNotificationEntity = this.save(userId, type,path, message); // 알림 내역 저장

        SseEmitter emitter = emitterMap.get(userId);

        log.info("targetEmitter:{}", emitter);
        if(emitter != null){
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(savedNotificationEntity.toModel())
                );
            } catch (IOException ex){
                emitterMap.remove(userId);
            }
        }
    }

    // 해당 알림 읽음 처리
    @Override
    public void markAyRead(Long id) {
        NotificationEntity notificationEntity = notificationRepository.findById(id);
        
        if(notificationEntity !=null){
            Notification notification = notificationEntity.toModel();
            notification.markAsRead(); // 읽음 처리
            
            notificationRepository.save(NotificationEntity.toEntity(notification)); // 저장
        }
    }

    // 읽은 알림 전체 삭제
    @Override
    public void removeAllReadNotifications(List<Long> ids) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"요청 권한이 없습니다.");
        }

    }

    // 선택 알림 삭제
    @Override
    public void removeReadNotification(List<Long> ids) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!email.contains("@")){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"요청 권한이 없습니다.");
        }

    }
}
