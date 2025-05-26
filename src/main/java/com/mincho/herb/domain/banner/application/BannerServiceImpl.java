package com.mincho.herb.domain.banner.application;


import com.mincho.herb.domain.banner.dto.BannerCreateRequestDTO;
import com.mincho.herb.domain.banner.dto.BannerResponseDTO;
import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.dto.BannerUpdateRequestDTO;
import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.repository.BannerRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


/**
 * {@link BannerService}의 구현체로, 배너 생성, 수정, 삭제, 조회 및 상태 변경 등의 비즈니스 로직을 담당한다.
 *
 * <p>트랜잭션 처리는 메서드 단위로 정의되며, 읽기 전용 조회 메서드에는 {@code @Transactional(readOnly = true)}가 적용된다.
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;
    private final S3Service s3Service;


    /**
     * 배너를 생성한다.
     *
     * @param request 배너 생성 요청 DTO
     * @return 생성된 배너의 응답 DTO
     */
    @Override
    @Transactional
    public BannerResponseDTO createBanner(BannerCreateRequestDTO request) {
        BannerEntity banner = BannerEntity.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .linkUrl(request.getLinkUrl())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(BannerStatusEnum.INACTIVE)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .isNewWindow(request.getIsNewWindow() != null ? request.getIsNewWindow() : false)
                .targetAudience(request.getTargetAudience())
                .clickCount(0)
                .viewCount(0)
                .createdBy(request.getCreatedBy())
                .build();

        // 날짜 기반 상태 설정
        banner.updateStatusBasedOnDate();

        BannerEntity savedBanner = bannerRepository.save(banner);
        log.info("배너가 생성되었습니다. ID: {}, 제목: {}", savedBanner.getId(), savedBanner.getTitle());

        return convertToResponse(savedBanner);
    }

    /**
     * 배너 정보를 수정한다.
     *
     * @param id 수정할 배너 ID
     * @param request 배너 수정 요청 DTO
     * @return 수정된 배너의 응답 DTO
     * @throws CustomHttpException 배너를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public BannerResponseDTO updateBanner(Long id, BannerUpdateRequestDTO request) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));

        // 필드 업데이트
        if (request.getTitle() != null) {
            banner.setTitle(request.getTitle());
        }
        if (request.getCategory() != null) {
            banner.setCategory(request.getCategory());
        }
        if (request.getImageUrl() != null) {
            banner.setImageUrl(request.getImageUrl());
        }
        if (request.getLinkUrl() != null) {
            banner.setLinkUrl(request.getLinkUrl());
        }
        if (request.getDescription() != null) {
            banner.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            banner.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            banner.setEndDate(request.getEndDate());
        }
        if (request.getSortOrder() != null) {
            banner.setSortOrder(request.getSortOrder());
        }
        if (request.getIsNewWindow() != null) {
            banner.setIsNewWindow(request.getIsNewWindow());
        }
        if (request.getTargetAudience() != null) {
            banner.setTargetAudience(request.getTargetAudience());
        }

        banner.setUpdatedBy(request.getUpdatedBy());

        // 날짜 기반 상태 업데이트
        banner.updateStatusBasedOnDate();

        BannerEntity savedBanner = bannerRepository.save(banner);
        log.info("배너가 수정되었습니다. ID: {}, 제목: {}", savedBanner.getId(), savedBanner.getTitle());

        return convertToResponse(savedBanner);
    }

    /**
     * 배너를 삭제한다.
     *
     * @param id 삭제할 배너 ID
     * @throws CustomHttpException 배너를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void deleteBanner(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));

        // S3에 저장된 배너 이미지 삭제
        if (banner.getImageUrl() != null) {
            String s3Key = s3Service.extractKeyFromUrl(banner.getImageUrl());
            s3Service.deleteKey(s3Key);
            log.info("S3에서 배너 이미지가 삭제되었습니다. Key: {}", s3Key);
        }

        bannerRepository.deleteById(id);
        log.info("배너가 삭제되었습니다. ID: {}", id);
    }

    /**
     * ID로 배너를 조회한다.
     *
     * @param id 조회할 배너 ID
     * @return 배너 응답 DTO
     * @throws CustomHttpException 배너를 찾을 수 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public BannerResponseDTO getBanner(Long id) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));

        return convertToResponse(banner);
    }

    /**
     * 검색 조건에 맞는 배너 목록을 조회한다.
     *
     * @param criteria 검색 조건 DTO
     * @param pageable 페이징 정보
     * @return 배너 응답 DTO의 페이지
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BannerResponseDTO> getBanners(BannerSearchCriteriaDTO criteria, Pageable pageable) {
        return bannerRepository.searchBanners(criteria, pageable)
                .map(this::convertToResponse);
    }

    /**
     * 활성화된 배너 목록을 조회한다.
     *
     * @return 활성 배너 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getActiveBanners() {
        return bannerRepository.findActiveBanners()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 카테고리의 활성화된 배너 목록을 조회한다.
     *
     * @param category 배너 카테고리
     * @return 해당 카테고리의 활성 배너 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getActiveBannersByCategory(String category) {
        return bannerRepository.findActiveBannersByCategory(category)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 배너 상태를 변경한다.
     *
     * @param id 배너 ID
     * @param status 변경할 상태
     * @return 상태가 변경된 배너 응답 DTO
     * @throws CustomHttpException 배너를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public BannerResponseDTO changeBannerStatus(Long id, BannerStatusEnum status) {
        BannerEntity banner = bannerRepository.findById(id)
                .orElseThrow(() -> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "배너를 찾을 수 없습니다. ID: " + id));

        banner.setStatus(status);
        BannerEntity savedBanner = bannerRepository.save(banner);

        log.info("배너 상태가 변경되었습니다. ID: {}, 상태: {}", id, status);

        return convertToResponse(savedBanner);
    }

    /**
     * 배너 정렬 순서를 변경한다.
     *
     * @param id 배너 ID
     * @param sortOrder 변경할 순서
     */
    @Override
    @Transactional
    public void updateBannerOrder(Long id, Integer sortOrder) {
        bannerRepository.updateBannerOrder(id, sortOrder);
        log.info("배너 순서가 변경되었습니다. ID: {}, 순서: {}", id, sortOrder);
    }


    /**
     * 배너 클릭을 처리한다 (클릭 수 증가).
     *
     * @param id 배너 ID
     */
    @Override
    @Transactional
    public void handleBannerClick(Long id) {
        bannerRepository.incrementClickCount(id);
        log.debug("배너 클릭 처리. ID: {}", id);
    }

    /**
     * 배너 노출을 처리한다 (조회 수 증가).
     *
     * @param id 배너 ID
     */
    @Override
    @Transactional
    public void handleBannerView(Long id) {
        bannerRepository.incrementViewCount(id);
        log.debug("배너 노출 처리. ID: {}", id);
    }

    /**
     * 주어진 기간 내에 만료될 예정인 배너 목록을 조회한다.
     *
     * @param days 현재로부터 며칠 이내 만료 예정인지 설정
     * @return 만료 예정 배너 리스트
     */
    @Override
    @Transactional(readOnly = true)
    public List<BannerResponseDTO> getBannersExpiringWithinDays(int days) {
        return bannerRepository.findBannersExpiringWithinDays(days)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }


    /**
     * 이미 만료된 배너의 상태를 일괄 업데이트한다.
     */
    @Override
    @Transactional
    public void updateExpiredBanners() {
        bannerRepository.updateExpiredBanners();
        log.info("만료된 배너 상태가 업데이트되었습니다.");
    }

    /**
     * {@link BannerEntity}를 {@link BannerResponseDTO}로 변환한다.
     *
     * @param banner 배너 엔티티
     * @return 배너 응답 DTO
     */
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
