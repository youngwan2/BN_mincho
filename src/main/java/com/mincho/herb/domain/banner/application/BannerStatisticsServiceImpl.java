package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerStatisticsResponseDTO;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.repository.BannerStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerStatisticsServiceImpl implements BannerStatisticsService {

    private final BannerStatisticsRepository bannerStatisticsRepository;

    @Override
    public BannerStatisticsResponseDTO getBannerStatistics() {
        Long totalBanners = bannerStatisticsRepository.countAllBanners();
        Long activeBanners = bannerStatisticsRepository.countByStatus(BannerStatusEnum.ACTIVE);
        Long inactiveBanners = bannerStatisticsRepository.countByStatus(BannerStatusEnum.INACTIVE);
        Long scheduledBanners = bannerStatisticsRepository.countByStatus(BannerStatusEnum.SCHEDULED);
        Long expiredBanners = bannerStatisticsRepository.countByStatus(BannerStatusEnum.EXPIRED);
        Long totalClicks = bannerStatisticsRepository.sumClickCount();
        Long totalViews = bannerStatisticsRepository.sumViewCount();
        double averageClickThroughRate = 0.0;
        if (totalViews != null && totalViews > 0 && totalClicks != null) {
            averageClickThroughRate = (double) totalClicks / totalViews * 100.0;
        }
        Long bannersExpiringWithinWeek = bannerStatisticsRepository.countExpiringWithinDays(7);
        return BannerStatisticsResponseDTO.builder()
                .totalBanners(totalBanners != null ? totalBanners : 0L)
                .activeBanners(activeBanners != null ? activeBanners : 0L)
                .inactiveBanners(inactiveBanners != null ? inactiveBanners : 0L)
                .scheduledBanners(scheduledBanners != null ? scheduledBanners : 0L)
                .expiredBanners(expiredBanners != null ? expiredBanners : 0L)
                .totalClicks(totalClicks != null ? totalClicks : 0L)
                .totalViews(totalViews != null ? totalViews : 0L)
                .averageClickThroughRate(averageClickThroughRate)
                .bannersExpiringWithinWeek(bannersExpiringWithinWeek != null ? bannersExpiringWithinWeek : 0L)
                .build();
    }
}
