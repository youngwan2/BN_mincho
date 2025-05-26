package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.entity.BannerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BannerRepository {
    BannerEntity save(BannerEntity banner);

    Optional<BannerEntity> findById(Long id);

    List<BannerEntity> findAll();

    void deleteById(Long id);

    List<BannerEntity> findActiveBanners();

    List<BannerEntity> findActiveBannersByCategory(String category);

    Page<BannerEntity> searchBanners(BannerSearchCriteriaDTO criteria, Pageable pageable);

    void updateBannerOrder(Long bannerId, Integer sortOrder);

    void incrementClickCount(Long bannerId);

    void incrementViewCount(Long bannerId);

    List<BannerEntity> findBannersExpiringWithinDays(int days);

    void updateExpiredBanners();
}
