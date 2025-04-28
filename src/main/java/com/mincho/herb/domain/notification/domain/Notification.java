package com.mincho.herb.domain.notification.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Notification {

    private Long id;
    private Long userId;
    private String type;
    private String message;
    private String path;
    private Boolean isRead;
    private LocalDateTime createdAt;


    // 읽음 처리
    public void markAsRead(){
        setIsRead(true);
    }
}
