package com.mincho.herb.domain.banner.application;

import com.mincho.herb.domain.banner.dto.BannerCreateRequestDTO;
import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerUpdateRequestDTO;
import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.repository.BannerRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BannerCommandServiceImpl implements BannerCommandService {
    private final BannerRepository bannerRepository;
    private final AuthUtils authUtils;
    private final S3Service s3Service;

    @Override
    @Transactional
    public BannerResponseDTO createBanner(BannerCreateRequestDTO request) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너를 생성할 권한이 없습니다.");
        }
        BannerEntity banner = BannerEntity.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .description(request.getDescription())
                .startDate(request.getStartDate().atStartOfDay())
                .endDate(request.getEndDate().atTime(23, 59, 59))
                .status(BannerStatusEnum.INACTIVE)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isNewWindow(request.getIsNewWindow() != null ? request.getIsNewWindow() : false)
                .targetAudience(request.getTargetAudience())
                .clickCount(0)
                .viewCount(0)
                .createdBy(request.getCreatedBy())
                .build();
        banner.updateStatusBasedOnDate(); // 배너 생성 시 상태 업데이트
        BannerEntity savedBanner = bannerRepository.save(banner);
        log.info("배너가 생성되었습니다. ID: {}, 제목: {}", savedBanner.getId(), savedBanner.getTitle());
        return convertToResponse(savedBanner);
    }

    @Override
    @Transactional
    public BannerResponseDTO updateBanner(Long id, BannerUpdateRequestDTO request) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너를 수정할 권한이 없습니다.");
        }
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));
        if (request.getTitle() != null) banner.setTitle(request.getTitle());
        if (request.getCategory() != null) banner.setCategory(request.getCategory());
        if (request.getImageUrl() != null) banner.setImageUrl(request.getImageUrl());
        if (request.getLinkUrl() != null) banner.setLinkUrl(request.getLinkUrl());
        if (request.getDescription() != null) banner.setDescription(request.getDescription());
        if (request.getStartDate() != null) banner.setStartDate(request.getStartDate().atStartOfDay());
        if (request.getEndDate() != null) banner.setEndDate(request.getEndDate().atTime(23, 59, 59));
        if (request.getSortOrder() != null) banner.setSortOrder(request.getSortOrder());
        if (request.getIsNewWindow() != null) banner.setIsNewWindow(request.getIsNewWindow());
        if (request.getTargetAudience() != null) banner.setTargetAudience(request.getTargetAudience());
        banner.setUpdatedBy(request.getUpdatedBy());
        banner.updateStatusBasedOnDate();
        BannerEntity savedBanner = bannerRepository.save(banner);
        log.info("배너가 수정되었습니다. ID: {}, 제목: {}", savedBanner.getId(), savedBanner.getTitle());
        return convertToResponse(savedBanner);
    }

    @Override
    @Transactional
    public void deleteBanner(Long id) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너를 삭제할 권한이 없습니다.");
        }
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));
        if (banner.getImageUrl() != null) {
            String s3Key = s3Service.extractKeyFromUrl(banner.getImageUrl());
            s3Service.deleteKey(s3Key);
            log.info("S3에서 배너 이미지가 삭제되었습니다. Key: {}", s3Key);
        }
        bannerRepository.deleteById(id);
        log.info("배너가 삭제되었습니다. ID: {}", id);
    }

    @Override
    @Transactional
    public BannerResponseDTO changeBannerStatus(Long id, BannerStatusEnum status) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너 상태를 변경할 권한이 없습니다.");
        }
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));
        banner.setStatus(status);
        BannerEntity savedBanner = bannerRepository.save(banner);
        log.info("배너 상태가 변경되었습니다. ID: {}, 상태: {}", id, status);
        return convertToResponse(savedBanner);
    }

    @Override
    @Transactional
    public void updateBannerOrder(Long id, Integer sortOrder) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너 순서를 변경할 권한이 없습니다.");
        }
        bannerRepository.updateBannerOrder(id, sortOrder);
        log.info("배너 순서가 변경되었습니다. ID: {}, 순서: {}", id, sortOrder);
    }

    @Override
    @Transactional
    public void updateExpiredBanners() {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "만료된 배너를 업데이트할 권한이 없습니다.");
        }
        bannerRepository.updateExpiredBanners();
        log.info("만료된 배너 상태가 업데이트되었습니다.");
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

