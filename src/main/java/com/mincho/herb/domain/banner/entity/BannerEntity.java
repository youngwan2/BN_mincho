package com.mincho.herb.domain.banner.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "banner")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class BannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 50)
    private String category; // "메인 배너", "사이드 배너", "팝업" 등

    @Column(nullable = true)
    private String imageUrl;

    @Column(length = 500)
    private String linkUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BannerStatusEnum status; // ACTIVE, INACTIVE, SCHEDULED, EXPIRED

    @Column(nullable = false)
    private Integer sortOrder; // 배너 순서

    @Column(nullable = false)
    private Boolean isNewWindow; // 새창 열기 여부

    @Column(length = 100)
    private String targetAudience; // 타겟 대상

    @Column(nullable = false)
    private Integer clickCount; // 클릭 수

    @Column(nullable = false)
    private Integer viewCount; // 노출 수

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(length = 50)
    private String createdBy; // 생성자

    @Column(length = 50)
    private String updatedBy; // 수정자


    // 배너가 현재 활성화되어 있는지 확인
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == BannerStatusEnum.ACTIVE &&
                now.isAfter(startDate) &&
                now.isBefore(endDate);
    }

    /**
     * 배너의 상태를 현재 날짜에 따라 업데이트합니다.
     * <p>- 현재 날짜가 시작일 이전이면 SCHEDULED로 설정</p>
     * <p>- 현재 날짜가 종료일 이후면 EXPIRED로 설정</p>
     * <p>- SCHEDULED 상태에서 현재 날짜가 시작일 이후면 ACTIVE로 설정</p>
     */
    public void updateStatusBasedOnDate() {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(startDate)) {
            this.status = BannerStatusEnum.SCHEDULED; // 예약 처리
        } else if (now.isAfter(endDate)) {
            this.status = BannerStatusEnum.EXPIRED; // 만료 처리
        } else if (this.status == BannerStatusEnum.SCHEDULED) {
            this.status = BannerStatusEnum.ACTIVE; // 활성화 처리
        }
    }
}