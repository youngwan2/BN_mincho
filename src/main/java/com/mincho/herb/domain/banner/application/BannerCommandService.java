package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerCreateRequestDTO;
import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerUpdateRequestDTO;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;

public interface BannerCommandService {
    BannerResponseDTO createBanner(BannerCreateRequestDTO request);
    BannerResponseDTO updateBanner(Long id, BannerUpdateRequestDTO request);
    void deleteBanner(Long id);
    BannerResponseDTO changeBannerStatus(Long id, BannerStatusEnum status);
    void updateBannerOrder(Long id, Integer sortOrder);
    void updateExpiredBanners();
}

