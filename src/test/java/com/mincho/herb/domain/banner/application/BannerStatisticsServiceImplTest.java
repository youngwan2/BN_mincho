package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerStatisticsResponseDTO;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.repository.BannerStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BannerStatisticsServiceImplTest {

    private BannerStatisticsRepository bannerStatisticsRepository;
    private BannerStatisticsServiceImpl bannerStatisticsService;

    @BeforeEach
    void setUp() {
        bannerStatisticsRepository = mock(BannerStatisticsRepository.class);
        bannerStatisticsService = new BannerStatisticsServiceImpl(bannerStatisticsRepository);
    }

    @Test
    void getBannerStatistics_returnsAggregatedDTO() {
        when(bannerStatisticsRepository.countAllBanners()).thenReturn(10L);
        when(bannerStatisticsRepository.countByStatus(BannerStatusEnum.ACTIVE)).thenReturn(5L);
        when(bannerStatisticsRepository.countByStatus(BannerStatusEnum.INACTIVE)).thenReturn(2L);
        when(bannerStatisticsRepository.countByStatus(BannerStatusEnum.SCHEDULED)).thenReturn(1L);
        when(bannerStatisticsRepository.countByStatus(BannerStatusEnum.EXPIRED)).thenReturn(2L);
        when(bannerStatisticsRepository.sumClickCount()).thenReturn(100L);
        when(bannerStatisticsRepository.sumViewCount()).thenReturn(1000L);
        when(bannerStatisticsRepository.countExpiringWithinDays(7)).thenReturn(3L);

        BannerStatisticsResponseDTO dto = bannerStatisticsService.getBannerStatistics();

        assertThat(dto.getTotalBanners()).isEqualTo(10L);
        assertThat(dto.getActiveBanners()).isEqualTo(5L);
        assertThat(dto.getInactiveBanners()).isEqualTo(2L);
        assertThat(dto.getScheduledBanners()).isEqualTo(1L);
        assertThat(dto.getExpiredBanners()).isEqualTo(2L);
        assertThat(dto.getTotalClicks()).isEqualTo(100L);
        assertThat(dto.getTotalViews()).isEqualTo(1000L);
        assertThat(dto.getAverageClickThroughRate()).isEqualTo(10.0);
        assertThat(dto.getBannersExpiringWithinWeek()).isEqualTo(3L);
    }
}
