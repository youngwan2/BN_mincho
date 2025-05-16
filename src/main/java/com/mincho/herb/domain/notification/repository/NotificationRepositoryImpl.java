package com.mincho.herb.domain.notification.repository;

import com.mincho.herb.domain.notification.entity.NotificationEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository{

    private final NotificationJpaRepository notificationJpaRepository;


    // 알림 저장
    @Override
    public NotificationEntity save(NotificationEntity notificationEntity) {
        return notificationJpaRepository.save(notificationEntity);
    }


    // 사용자의 모든 알림 조회
    @Override
    public List<NotificationEntity> findAllByUserId(Long userId, Pageable pageable) {
        return notificationJpaRepository.findAllByUserId(userId, pageable).stream().toList();
    }

    // 단일 알림 찾기
    @Override
    public Optional<NotificationEntity> findById(Long id) {
        return notificationJpaRepository.findById(id);
    }

    // 알림 개수 통계
    @Override
    public Long countByUserId(Long userId) {
        return notificationJpaRepository.countByUserId(userId);
    }

    // 선택 알림 삭제
    @Override
    public void deleteAllByIds(List<Long> ids) {
        notificationJpaRepository.deleteAllByIds(ids);

    }
    // 읽은 알림 삭제
    @Override
    public void deleteAllByIsReadTrue(Long userId) {
        notificationJpaRepository.deleteAllByIdsAndIsReadTrueAndUserId(userId);
    }

    // 읽지 않은 알림이 하나라도 존재하면 false
    @Override
    public Boolean existsByUserIdAndIsReadFalse(Long userId) {
        return notificationJpaRepository.existsByUserIdAndIsReadFalse(userId);
    }
}
