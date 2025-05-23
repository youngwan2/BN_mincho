package com.mincho.herb.domain.notification.repository;

import com.mincho.herb.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository{

    NotificationEntity save(NotificationEntity notificationEntity);
    List<NotificationEntity> findAllByUserId(Long userId, Pageable pageable);
    Optional<NotificationEntity> findById(Long id);
    Long countByUserId(Long userId);
    void deleteAllByIds(List<Long> ids);
    void deleteAllByIsReadTrue(Long userId);
    Boolean existsByUserIdAndIsReadFalse(Long userId);
}
