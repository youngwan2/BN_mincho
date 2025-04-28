package com.mincho.herb.domain.notification.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.notification.domain.Notification;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "notification",
        indexes = {
                @Index(name = "idx_user_id", columnList = "userId"),
                @Index(name = "idx_is_read", columnList = "isRead"),
                @Index(name = "idx_created_at", columnList = "createdAt")
        }
)
public class NotificationEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String type;
    private String path;
    private String message;
    private Boolean isRead;

    // 엔티티로
    public static NotificationEntity toEntity(Notification notification){
        NotificationEntity notificationEntity = new NotificationEntity();

        notificationEntity.setId(notification.getId());
        notificationEntity.setUserId(notification.getUserId());
        notificationEntity.setType(notification.getType());
        notificationEntity.setPath(notification.getPath());
        notificationEntity.setMessage(notification.getMessage());
        notificationEntity.setIsRead(notification.getIsRead());
        notificationEntity.setCreatedAt(notification.getCreatedAt());

        return notificationEntity;
    }

    // 모델로
    public Notification toModel(){
        return Notification.builder()
                .id(this.id)
                .type(this.type)
                .path(this.path)
                .userId(this.userId)
                .message(this.message)
                .createdAt(super.getCreatedAt())
                .isRead(this.isRead)
                .build();
    }
}
