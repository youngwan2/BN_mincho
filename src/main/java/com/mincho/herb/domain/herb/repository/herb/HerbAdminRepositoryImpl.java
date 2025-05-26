package com.mincho.herb.domain.herb.repository.herb;

import com.mincho.herb.domain.herb.dto.*;
import com.mincho.herb.domain.herb.entity.HerbEntity;
import com.mincho.herb.domain.herb.entity.QHerbEntity;
import com.mincho.herb.domain.herb.entity.QHerbTagEntity;
import com.mincho.herb.domain.herb.entity.QHerbViewsEntity;
import com.mincho.herb.domain.like.entity.QHerbLikeEntity;
import com.mincho.herb.domain.tag.entity.QTagEntity;
import com.mincho.herb.domain.tag.entity.TagTypeEnum;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.MathUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Repository
@RequiredArgsConstructor
public class HerbAdminRepositoryImpl implements HerbAdminRepository{

    private final HerbJpaRepository herbJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public HerbEntity save(HerbEntity herbEntity) {
        return herbJpaRepository.save(herbEntity);
    }

    @Override
    public void saveAll(List<HerbEntity> herbs) {
        herbJpaRepository.saveAll(herbs);
    }

    @Override
    public void deleteById(Long id) {
        herbJpaRepository.deleteById(id);
    }

    // 약초 이미지 제거
    @Override
    public HerbEntity removeHerbImagesByHerbId(Long herbId) {

        HerbEntity herbEntity = herbJpaRepository.findById(herbId).orElse(null);

        if(herbEntity == null){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 약초 데이터가 존재하지 않습니다.");
        }

        herbEntity.setImgUrl1(null);
        herbEntity.setImgUrl2(null);
        herbEntity.setImgUrl3(null);
        herbEntity.setImgUrl4(null);
        herbEntity.setImgUrl5(null);
        herbEntity.setImgUrl6(null);

        return herbEntity;

    }

    // 약초 리스트
    @Override
    public HerbAdminResponseDTO findHerbList(String keyword, Pageable pageable, HerbFilteringConditionDTO herbFilteringConditionDTO, HerbSort herbSort) {
        QHerbEntity herb = QHerbEntity.herbEntity;
        QHerbTagEntity herbTag = QHerbTagEntity.herbTagEntity;
        QTagEntity tag = QTagEntity.tagEntity;
        QHerbViewsEntity herbViews = QHerbViewsEntity.herbViewsEntity;
        QHerbLikeEntity herbLike = QHerbLikeEntity.herbLikeEntity;

        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // 검색 기준
        if(keyword != null && !keyword.isEmpty()){
            booleanBuilder.and(
                    herb.cntntsSj.like("%"+keyword+"%"))
                    .or(herb.bneNm.like("%"+keyword+"%"))
                    .or(herb.flowering.like("%"+keyword+"%"))
                    .or(herb.growthForm.like("%"+keyword+"%"))
                    .or(herb.habitat.like("%"+keyword+"%"))
                    .or(herb.harvest.like("%"+keyword+"%"));
        }

        // 카테고리 기준
        if(herbFilteringConditionDTO.getBneNm() != null && !herbFilteringConditionDTO.getBneNm().isEmpty()){
            booleanBuilder.and(herb.bneNm.eq(herbFilteringConditionDTO.getBneNm()));
        }

        // 태그 타입 기준
        if(herbFilteringConditionDTO.getTagType() != null && !herbFilteringConditionDTO.getTagType().isEmpty()){
            booleanBuilder.and(herbTag.tag.tagType.eq(TagTypeEnum.valueOf(herbFilteringConditionDTO.getTagType())));
        } else {
            // 태그 타입이 없으면 모든 태그 타입을 포함
            booleanBuilder.and(herbTag.tag.tagType.in(TagTypeEnum.EFFECT, TagTypeEnum.SIDE_EFFECT));
        }

        // 태그 기준
        if(herbFilteringConditionDTO.getTag() != null && !herbFilteringConditionDTO.getTag().isEmpty()){
            herbFilteringConditionDTO.getTag().forEach(
                    tagName -> booleanBuilder.and(herbTag.tag.name.eq(tagName))
            );
        }

        // 정렬 기준
        OrderSpecifier<?> orderSpecifier = null;
        if(herbSort.getSort() != null && !herbSort.getSort().isEmpty()){
            orderSpecifier = switch (herbSort.getSort()) {
                case "recent" -> // 최신순
                        herb.createdAt.desc();
                case "popular" -> // 인기순(조회수 기준)
                        herbViews.viewCount.desc();
                case "like" -> // 추천순(좋아요 기준)
                        herbLike.count().desc();
                default -> throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 정렬 기준입니다.");
            };
        } else {
            // 기본 정렬은 최신순
            orderSpecifier = herb.createdAt.desc();
        }

        // 페이징 적용된 id 리스트 조회
        List<Long> herbIds = jpaQueryFactory
                .select(herb.id)
                .from(herb)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        booleanBuilder.and(herb.id.in(herbIds));

        log.info("herbIds:{}", herbIds);

        // 페이징된 herbId 에 대한 조회
        List<HerbAdminDTO> herbList = jpaQueryFactory.selectFrom(herb)
                .leftJoin(herbTag).on(herbTag.herb.eq(herb))
                .leftJoin(tag).on(tag.eq(herbTag.tag))
                .leftJoin(herbViews).on(herbViews.herb.eq(herb))
                .where(booleanBuilder)
                .transform(
                        GroupBy.groupBy(
                                herb.id, herb.bneNm, herb.cntntsSj, herb.hbdcNm,
                                herb.imgUrl1, herb.imgUrl2, herb.imgUrl3, herb.imgUrl4, herb.imgUrl5, herb.imgUrl6,
                                herb.prvateTherpy, herb.useeRegn, herb.growthForm, herb.flowering,
                                herb.habitat, herb.harvest,
                                herbViews.viewCount
                        ).list(
                            Projections.constructor(HerbAdminDTO.class,
                                    herb.id, herb.bneNm, herb.cntntsSj, herb.hbdcNm,
                                    herb.imgUrl1, herb.imgUrl2, herb.imgUrl3, herb.imgUrl4, herb.imgUrl5, herb.imgUrl6,
                                    herb.prvateTherpy, herb.useeRegn, herb.growthForm, herb.flowering,
                                    herb.habitat, herb.harvest,
                                    herbViews.viewCount,
                                    GroupBy.list(Projections.fields(TagDTO.class,
                                            tag.id.as("id"),
                                            tag.name.as("name"),
                                            tag.tagType.as("tagType")
                                    ))
                )));



        // 전체 개수
        Long totalCount = jpaQueryFactory.select(herb.count()).from(herb)
                .leftJoin(herbTag).on(herbTag.herb.eq(herb))
                .leftJoin(tag).on(tag.eq(herbTag.tag))
                .leftJoin(herbViews).on(herbViews.herb.eq(herb))
                .leftJoin(herbLike).on(herbLike.herb.eq(herb))
                .where(booleanBuilder)
                .fetchOne();

        return HerbAdminResponseDTO.builder()
                .herbs(herbList)
                .totalCount(totalCount)
                .build();
    }


    // 약초 통계
    @Override
    public HerbStatisticsDTO findHerbStatics() {
        QHerbEntity herb = QHerbEntity.herbEntity;
        LocalDate now = LocalDate.now();

        // 이번 달 시작일
        LocalDateTime startOfMonth = now.withDayOfMonth(1).atStartOfDay();
        // 저번 달 시작일
        LocalDateTime startOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();
        // 저번 달 종료일
        LocalDateTime endOfPreviousMonth = startOfMonth.minusNanos(1);

        // 약초 총 개수
        Long totalCount = jpaQueryFactory.select(herb.count())
                .from(herb)
                .fetchOne();

        // 현재 약초 개수
        Long currentCount = jpaQueryFactory.select(herb.count())
                .from(herb)
                .where(herb.createdAt.goe(startOfMonth))
                .fetchOne();

        // 저번 달 약초 개수
        Long previousCount = jpaQueryFactory.select(herb.count())
                .from(herb)
                .where(herb.createdAt.between(startOfPreviousMonth, endOfPreviousMonth))
                .fetchOne();

        // 증감율
        double growthRate = MathUtil.getGrowthRate(previousCount, currentCount);

        return HerbStatisticsDTO.builder()
                .totalCount(totalCount)
                .currentMonthCount(currentCount)
                .previousMonthCount(previousCount)
                .growthRate(growthRate)
                .build();
    }


    // 일별 약초 등록 통계
    @Override
    public List<DailyHerbStatisticsDTO> findDailyHerbStatistics(LocalDate startDate, LocalDate endDate) {
        QHerbEntity herb = QHerbEntity.herbEntity;

        StringTemplate date = Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", herb.createdAt);

        return jpaQueryFactory
                .select(Projections.constructor(DailyHerbStatisticsDTO.class,
                        date,
                        herb.id.count().as("herbCount")
                ))
                .from(herb)
                .where(herb.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .groupBy(date)
                .orderBy(date.asc())
                .fetch();
    }
}
