package com.mincho.herb.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationsResponse {

    private List<NotificationDTO> notifications;
    private Long totalCount;
    private int nextPage;

}
