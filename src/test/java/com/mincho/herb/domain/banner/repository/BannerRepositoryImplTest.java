package com.mincho.herb.domain.banner.repository;

import com.mincho.herb.domain.banner.entity.BannerEntity;
import com.mincho.herb.domain.banner.entity.BannerStatusEnum;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BannerRepositoryImplTest {

    private BannerRepositoryImpl bannerRepository;

    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private BannerJpaRepository bannerJpaRepository;

    private BannerEntity banner1;
    private BannerEntity banner2;

    @BeforeEach
    void setUp() {
        bannerRepository = new BannerRepositoryImpl(bannerJpaRepository, jpaQueryFactory);

        bannerJpaRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();

        banner1 = BannerEntity.builder()
                .title("Test Banner 1")
                .category("MAIN")
                .imageUrl("https://example.com/banner1.jpg")
                .linkUrl("https://example.com/1")
                .description("Banner 1 description")
                .startDate(now.minusDays(1))
                .endDate(now.plusDays(5))
                .status(BannerStatusEnum.ACTIVE)
                .sortOrder(1)
                .isNewWindow(true)
                .targetAudience("ALL")
                .clickCount(0)
                .viewCount(0)
                .createdAt(now.minusDays(2))
                .updatedAt(now.minusDays(1))
                .createdBy("tester")
                .updatedBy("tester")
                .build();

        banner2 = BannerEntity.builder()
                .title("Test Banner 2")
                .category("SUB")
                .imageUrl("https://example.com/banner2.jpg")
                .linkUrl("https://example.com/2")
                .description("Banner 2 description")
                .startDate(now.minusDays(2))
                .endDate(now.plusDays(2))
                .status(BannerStatusEnum.INACTIVE)
                .sortOrder(2)
                .isNewWindow(false)
                .targetAudience("MEMBER")
                .clickCount(0)
                .viewCount(0)
                .createdAt(now.minusDays(3))
                .updatedAt(now.minusDays(2))
                .createdBy("tester")
                .updatedBy("tester")
                .build();

        banner1 = bannerRepository.save(banner1);
        banner2 = bannerRepository.save(banner2);
    }

    /**
     * save와 findById를 사용하여 저장한 배너를 ID로 조회할 수 있는지 검증합니다.
     */
    @Test
    void saveAndFindById_returnsSavedBanner_whenBannerIsSaved() {
        Optional<BannerEntity> found = bannerRepository.findById(banner1.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Test Banner 1");
    }

    /**
     * findAll을 사용하여 전체 배너 목록을 조회할 수 있는지 검증합니다.
     */
    @Test
    void findAll_returnsAllBanners_whenBannersExist() {
        List<BannerEntity> all = bannerRepository.findAll();
        assertThat(all).hasSize(2);
    }

    /**
     * deleteById를 사용하여 배너를 삭제한 후 조회되지 않는지 검증합니다.
     */
    @Test
    void deleteById_removesBanner_whenBannerExists() {
        bannerRepository.deleteById(banner1.getId());
        assertThat(bannerRepository.findById(banner1.getId())).isNotPresent();
    }
}
