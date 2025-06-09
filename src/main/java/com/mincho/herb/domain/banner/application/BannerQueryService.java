package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BannerQueryService {
    BannerResponseDTO getBanner(Long id);
    Page<BannerResponseDTO> getBanners(BannerSearchCriteriaDTO criteria, Pageable pageable);
    List<BannerResponseDTO> getActiveBanners();
    List<BannerResponseDTO> getActiveBannersByCategory(String category);
    List<BannerResponseDTO> getBannersExpiringWithinDays(int days);
}

