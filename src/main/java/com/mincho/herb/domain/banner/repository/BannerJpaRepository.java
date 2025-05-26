package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BannerJpaRepository extends JpaRepository<BannerEntity, Long> {

    // 카테고리별 배너 조회
    List<BannerEntity> findByCategoryAndStatusOrderBySortOrderAsc(String category, BannerStatusEnum status);

    // 활성화된 배너 조회 (기간 포함)
    @Query("SELECT b FROM BannerEntity b WHERE b.status = 'ACTIVE' AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.sortOrder ASC")
    List<BannerEntity> findActiveBanners(@Param("now") LocalDateTime now);

    // 카테고리별 활성화된 배너 조회
    @Query("SELECT b FROM BannerEntity b WHERE b.category = :category AND b.status = 'ACTIVE' AND b.startDate <= :now AND b.endDate >= :now ORDER BY b.sortOrder ASC")
    List<BannerEntity> findActiveBannersByCategory(@Param("category") String category, @Param("now") LocalDateTime now);

    // 상태별 배너 조회 (페이징)
    Page<BannerEntity> findByStatusOrderByCreatedAtDesc(BannerStatusEnum status, Pageable pageable);

    // 제목으로 검색
    Page<BannerEntity> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String title, Pageable pageable);

    // 기간으로 검색
    @Query("SELECT b FROM BannerEntity b WHERE b.startDate >= :startDate AND b.endDate <= :endDate ORDER BY b.createdAt DESC")
    Page<BannerEntity> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate,
                                 Pageable pageable);

    // 만료 예정 배너 조회
    @Query("SELECT b FROM BannerEntity b WHERE b.status = 'ACTIVE' AND b.endDate BETWEEN :now AND :endTime")
    List<BannerEntity> findBannersExpiringBetween(@Param("now") LocalDateTime now, @Param("endTime") LocalDateTime endTime);
}
