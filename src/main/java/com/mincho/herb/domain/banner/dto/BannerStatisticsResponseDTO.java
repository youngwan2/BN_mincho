package com.mincho.herb.domain.banner.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerStatisticsResponseDTO {
    private Long totalBanners;
    private Long activeBanners;
    private Long inactiveBanners;
    private Long scheduledBanners;
    private Long expiredBanners;
    private Long totalClicks;
    private Long totalViews;
    private Double averageClickThroughRate;
    private Long bannersExpiringWithinWeek;
}
