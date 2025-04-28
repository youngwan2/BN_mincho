package com.mincho.herb.domain.notification.api;


import com.mincho.herb.domain.notification.application.NotificationService;
import com.mincho.herb.domain.notification.dto.NotificationsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    // SSE 설정
    @GetMapping(value ="/notification/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(){
        return notificationService.subscribe();
    }

    // 알림 목록 조회
    @GetMapping("/notifications")
    public ResponseEntity<NotificationsResponse> getNotifications(
            @RequestParam int page,
            @RequestParam int size
    ){
        NotificationsResponse response =notificationService.getNotifications(page, size);

        return ResponseEntity.ok(response);
    }

    // 알림 읽음 처리
    @PatchMapping("/notifications/read/{id}")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id
    ){
        notificationService.markAyRead(id);

        return ResponseEntity.noContent().build();
    }

    // 알림 삭제 처리
    @DeleteMapping("/notifications/select")
    public ResponseEntity<Void> removeNotification(
            @PathVariable List<Long> ids
    ){
        notificationService.removeReadNotification(ids);
        return ResponseEntity.noContent().build();
    }

    // 읽은 알림 전체 삭제 처리
    @DeleteMapping("/notifications/read")
    public ResponseEntity<Void> removeAllReadNotifications(
            @PathVariable List<Long> ids
    ){
        notificationService.removeAllReadNotifications(ids);
        return ResponseEntity.noContent().build();
    }
}
