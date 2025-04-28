package com.mincho.herb.domain.notification.repository;

import com.mincho.herb.domain.notification.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, Long> {

    @Query("SELECT n FROM NotificationEntity n WHERE userId = :userId")
    Page<NotificationEntity> findAllByUserId(Long userId, Pageable pageable);


    // 읽은 상태의 알림 모두 제거
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.id IN :ids AND n.isRead = true ")
    void deleteAllByIdsAndIsReadTrue(List<Long> ids);

    // 선택한 알림 제거
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.id IN :ids AND n.isRead = true ")
    void deleteAllByIds(List<Long> ids);

    Long countByUserId(Long userId);
}
