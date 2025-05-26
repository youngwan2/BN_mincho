package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerCreateRequestDTO;
import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.dto.BannerUpdateRequestDTO;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BannerService {
    BannerResponseDTO createBanner(BannerCreateRequestDTO request);

    BannerResponseDTO updateBanner(Long id, BannerUpdateRequestDTO request);

    void deleteBanner(Long id);

    BannerResponseDTO getBanner(Long id);

    Page<BannerResponseDTO> getBanners(BannerSearchCriteriaDTO criteria, Pageable pageable);

    List<BannerResponseDTO> getActiveBanners();

    List<BannerResponseDTO> getActiveBannersByCategory(String category);

    void updateBannerOrder(Long id, Integer sortOrder);

    void handleBannerClick(Long id);

    void handleBannerView(Long id);

    List<BannerResponseDTO> getBannersExpiringWithinDays(int days);

    void updateExpiredBanners();

    BannerResponseDTO changeBannerStatus(Long id, BannerStatusEnum status);
}
