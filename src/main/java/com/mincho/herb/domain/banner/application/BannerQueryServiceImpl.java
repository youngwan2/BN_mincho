package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerQueryServiceImpl implements BannerQueryService {
    private final BannerRepository bannerRepository;

    @Override
    @Transactional(readOnly = true)
    public BannerResponseDTO getBanner(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("배너를 찾을 수 없습니다. ID: " + id));
        return convertToResponse(banner);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BannerResponseDTO> getBanners(BannerSearchCriteriaDTO criteria, Pageable pageable) {
        return bannerRepository.searchBanners(criteria, pageable)
                .map(this::convertToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getActiveBanners() {
        return bannerRepository.findActiveBanners()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getActiveBannersByCategory(String category) {
        return bannerRepository.findActiveBannersByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getBannersExpiringWithinDays(int days) {
        return bannerRepository.findBannersExpiringWithinDays(days)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private BannerResponseDTO convertToResponse(BannerEntity banner) {
        return BannerResponseDTO.builder()
                .id(banner.getId())
                .title(banner.getTitle())
                .category(banner.getCategory())
                .imageUrl(banner.getImageUrl())
                .linkUrl(banner.getLinkUrl())
                .description(banner.getDescription())
                .startDate(banner.getStartDate())
                .endDate(banner.getEndDate())
                .status(banner.getStatus())
                .sortOrder(banner.getSortOrder())
                .isNewWindow(banner.getIsNewWindow())
                .targetAudience(banner.getTargetAudience())
                .clickCount(banner.getClickCount())
                .viewCount(banner.getViewCount())
                .createdAt(banner.getCreatedAt())
                .updatedAt(banner.getUpdatedAt())
                .createdBy(banner.getCreatedBy())
                .updatedBy(banner.getUpdatedBy())
                .build();
    }
}

