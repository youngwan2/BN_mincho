package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.dto.BannerSearchCriteriaDTO;
import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.entity.QBannerEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 배너에 대한 복합적인 검색 및 동적 쿼리 처리를 담당하는 커스텀 리포지토리 구현체.
 * JPA + QueryDSL 기반의 동적 검색, 날짜 필터링, 정렬, 클릭/뷰 카운트 증가 등의 기능을 포함합니다.
 *
 * @author YoungWan Kim
 */
@Repository
@RequiredArgsConstructor
public class BannerRepositoryImpl implements BannerRepository {

    private final BannerJpaRepository bannerJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;


    /**
     * 배너 저장
     *
     * @param banner 저장할 배너 엔티티
     * @return 저장된 배너 엔티티
     */
    @Override
    public BannerEntity save(BannerEntity banner) {
        return bannerJpaRepository.save(banner);
    }

    /**
     * ID로 배너 조회
     *
     * @param id 배너 ID
     * @return 해당 ID의 배너 Optional
     */
    @Override
    public Optional<BannerEntity> findById(Long id) {
        return bannerJpaRepository.findById(id);
    }

    /**
     * 전체 배너 조회
     *
     * @return 모든 배너 리스트
     */
    @Override
    public List<BannerEntity> findAll() {
        return bannerJpaRepository.findAll();
    }

    /**
     * ID로 배너 삭제
     *
     * @param id 삭제할 배너 ID
     */
    @Override
    public void deleteById(Long id) {
        bannerJpaRepository.deleteById(id);
    }

    /**
     * 현재 시각 기준으로 활성화된 배너 목록 조회
     *
     * @return 활성 배너 리스트
     */
    @Override
    public List<BannerEntity> findActiveBanners() {
        return bannerJpaRepository.findActiveBanners(LocalDateTime.now());
    }

    /**
     * 카테고리 기준으로 현재 활성화된 배너 조회
     *
     * @param category 배너 카테고리
     * @return 해당 카테고리의 활성 배너 리스트
     */
    @Override
    public List<BannerEntity> findActiveBannersByCategory(String category) {
        return bannerJpaRepository.findActiveBannersByCategory(category, LocalDateTime.now());
    }

    /**
     * 배너 검색 (제목, 카테고리, 상태를 기준으로)
     *
     * @param criteria 배너 검색 조건
     * @param pageable 페이징 정보
     * @return 조건에 맞는 배너 페이지
     */
    @Override
    public Page<BannerEntity> searchBanners(BannerSearchCriteriaDTO criteria, Pageable pageable) {
        QBannerEntity banner = QBannerEntity.bannerEntity;

        BooleanBuilder whereClause = new BooleanBuilder();
        if (criteria.getTitle() != null && !criteria.getTitle().trim().isEmpty()) {
            whereClause.and(banner.title.containsIgnoreCase(criteria.getTitle()));
        }
        if (criteria.getCategory() != null && !criteria.getCategory().trim().isEmpty()) {
            whereClause.and(banner.category.eq(criteria.getCategory()));
        }
        if (criteria.getStatus() != null) {
            whereClause.and(banner.status.eq(criteria.getStatus()));
        }
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            whereClause.and(banner.startDate.goe(criteria.getStartDate())
                    .and(banner.endDate.loe(criteria.getEndDate())));
        }

        List<BannerEntity> banners = jpaQueryFactory.selectFrom(banner)
                .where(whereClause)
                .orderBy(banner.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(banner.count())
                .from(banner)
                .where(whereClause)
                .fetchOne();

        return new PageImpl<>(banners, pageable, total != null ? total : 0L);
    }

    /**
     * 특정 배너의 정렬 순서(sortOrder)를 갱신
     *
     * @param bannerId  배너 ID
     * @param sortOrder 새로운 정렬 순서
     */
    @Override
    public void updateBannerOrder(Long bannerId, Integer sortOrder) {
        QBannerEntity banner = QBannerEntity.bannerEntity;

        jpaQueryFactory.update(banner)
                .set(banner.sortOrder, sortOrder)
                .where(banner.id.eq(bannerId))
                .execute();
    }

    /**
     * 특정 배너의 클릭 수를 1 증가
     *
     * @param bannerId 배너 ID
     */
    @Override
    public void incrementClickCount(Long bannerId) {
        QBannerEntity banner = QBannerEntity.bannerEntity;

        jpaQueryFactory.update(banner)
                .set(banner.clickCount, banner.clickCount.add(1))
                .where(banner.id.eq(bannerId))
                .execute();
    }

    /**
     * 특정 배너의 조회 수를 1 증가
     *
     * @param bannerId 배너 ID
     */
    @Override
    public void incrementViewCount(Long bannerId) {
        QBannerEntity banner = QBannerEntity.bannerEntity;

        jpaQueryFactory.update(banner)
                .set(banner.viewCount, banner.viewCount.add(1))
                .where(banner.id.eq(bannerId))
                .execute();
    }

    /**
     * 지정한 일 수 이내에 만료될 예정인 활성 배너 목록 조회
     *
     * @param days 앞으로 며칠 이내 만료될지를 나타냄
     * @return 만료 임박 배너 리스트
     */
    @Override
    public List<BannerEntity> findBannersExpiringWithinDays(int days) {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        LocalDateTime now = LocalDateTime.now(); // 현재 시간
        LocalDateTime endTime = now.plusDays(days); // 만료 예정일 계산

        return jpaQueryFactory.selectFrom(banner)
                .where(banner.status.eq(BannerStatusEnum.ACTIVE)
                        .and(banner.endDate.between(now, endTime)))
                .fetch();
    }

    /**
     * 현재 시간(now)이 종료일보다 지난 활성 배너들을 만료 상태(EXPIRED)로 일괄 업데이트
     */
    @Override
    public void updateExpiredBanners() {
        QBannerEntity banner = QBannerEntity.bannerEntity;

        jpaQueryFactory.update(banner)
                .set(banner.status, BannerStatusEnum.EXPIRED)
                .where(banner.status.eq(BannerStatusEnum.ACTIVE)
                        .and(banner.endDate.lt(LocalDateTime.now())))
                .execute();
    }
}
