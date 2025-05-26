package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.entity.BannerStatusEnum;

public interface BannerStatisticsRepository {
    Long countAllBanners();
    Long countByStatus(BannerStatusEnum status);
    Long sumClickCount();
    Long sumViewCount();
    Long countExpiringWithinDays(int days);
}
