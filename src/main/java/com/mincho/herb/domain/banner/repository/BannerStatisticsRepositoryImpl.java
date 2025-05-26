package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.mincho.herb.domain.banner.entity.QBannerEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class BannerStatisticsRepositoryImpl implements BannerStatisticsRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Long countAllBanners() {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        return jpaQueryFactory.select(banner.count()).from(banner).fetchOne();
    }

    @Override
    public Long countByStatus(BannerStatusEnum status) {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        return jpaQueryFactory.select(banner.count()).from(banner).where(banner.status.eq(status)).fetchOne();
    }

    @Override
    public Long sumClickCount() {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        Integer sum= jpaQueryFactory.select(banner.clickCount.sum()).from(banner).fetchOne();
        return sum != null ? sum : 0L;
    }

    @Override
    public Long sumViewCount() {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        Integer sum= jpaQueryFactory.select(banner.viewCount.sum()).from(banner).fetchOne();
        return sum != null ? sum : 0L;
    }

    @Override
    public Long countExpiringWithinDays(int days) {
        QBannerEntity banner = QBannerEntity.bannerEntity;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = now.plusDays(days);
        return jpaQueryFactory.select(banner.count())
                .from(banner)
                .where(banner.status.eq(BannerStatusEnum.ACTIVE)
                        .and(banner.endDate.between(now, end)))
                .fetchOne();
    }
}
