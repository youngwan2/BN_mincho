package com.mincho.herb.domain.notification.application;


import com.mincho.herb.domain.notification.dto.NotificationsResponse;
import com.mincho.herb.domain.notification.entity.NotificationEntity;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


public interface NotificationService {
    SseEmitter subscribe();
    NotificationEntity save(Long userId, String type,String path, String message);
    NotificationsResponse getNotifications(int page, int size);
    /**
     * 대상이 되는 유저에게 알림을 전송한다.
     * @Param userId  알림을 받을 유저의 id
     * @Param type 알림 유형(post, comment, notice, ...)
     * @Param path 컨텐츠 경로
     * @Param message 메시지
     * */
    void sendNotification(Long userId, String type, String path, String message);
    void markAyRead(Long id);
    void removeAllReadNotifications(List<Long> ids);
    void removeReadNotification(List<Long> ids);
}
