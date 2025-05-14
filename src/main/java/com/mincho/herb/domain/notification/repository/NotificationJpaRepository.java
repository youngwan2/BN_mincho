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
    @Query("DELETE FROM NotificationEntity n WHERE n.userId =:userId AND n.isRead = true ")
    void deleteAllByIdsAndIsReadTrueAndUserId(Long userId);

    // 선택한 알림 제거
    @Modifying
    @Query("DELETE FROM NotificationEntity n WHERE n.id IN :ids AND n.isRead = true ")
    void deleteAllByIds(List<Long> ids);

    Long countByUserId(Long userId);

    // 읽지 않은 알림이 하나라도 존재하면 false
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN false ELSE true END FROM NotificationEntity n WHERE n.userId = :userId AND n.isRead = false")
    Boolean existsByUserIdAndIsReadFalse(Long userId);
}
