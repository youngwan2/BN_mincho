package com.mincho.herb.domain.post.repository.postStatistics;

import com.mincho.herb.domain.post.dto.DailyPostStatisticsDTO;
import com.mincho.herb.domain.post.dto.PostCategoryInfoDTO;
import com.mincho.herb.domain.post.dto.PostStatisticsDTO;
import com.mincho.herb.domain.post.entity.QPostEntity;
import com.mincho.herb.domain.post.repository.post.PostJpaRepository;
import com.mincho.herb.global.util.MathUtil;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostStatisticsRepositoryImpl implements PostStatisticsRepository {

    private final PostJpaRepository postJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;


    // 사용자 별 게시글 수
    @Override
    public Long countByUserId(Long userId) {
        return  postJpaRepository.countByUserId(userId);
    }



    // 카테고리 통계
    @Override
    public List<PostCategoryInfoDTO> countsByCategory() {
        return postJpaRepository.countPostsByCategory();
    }

    // 포스트 통계
    @Override
    public PostStatisticsDTO findPostStatics() {

        QPostEntity post = QPostEntity.postEntity;

        LocalDate now = LocalDate.now();

        // 현재 달의 첫 번째 날
        LocalDate firstDayOfCurrentMonth = now.withDayOfMonth(1);

        // 이전 달의 첫 번째 날
        LocalDate firstDayOfPreviousMonth = firstDayOfCurrentMonth.minusMonths(1);

        // 현재 달의 첫 번째 날의 시작 00:00:00
        LocalDateTime startOfCurrentMonth =  firstDayOfCurrentMonth.atStartOfDay();

        // 이전 달의 첫 번째 날의 시작 00:00:00
        LocalDateTime startOfPreviousMonth = firstDayOfPreviousMonth.atStartOfDay();

        // 현재 달의 시작 00:00:00 에서 00:00:01 을 뺀 값 -> 전월 말일 23:59:59.999999999
        LocalDateTime endOfPreviousMonth = startOfCurrentMonth.minusNanos(1);

        // 전체 포스트 개수 (삭제되지 않은 게시글만)
        Long totalCount = jpaQueryFactory.select(post.count())
                .from(post)
                .where(post.isDeleted.isFalse())
                .fetchOne();

        // 이번 달의 포스트 개수 (삭제되지 않은 게시글만)
        Long currentMonthCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.createdAt.goe(startOfCurrentMonth)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        // 저번 달의 포스트 개수 (삭제되지 않은 게시글만)
        Long previousMonthCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.createdAt.between(startOfPreviousMonth, endOfPreviousMonth)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();

        // 증감율 계산
        double growthRate = MathUtil.getGrowthRate(previousMonthCount, currentMonthCount);

        return PostStatisticsDTO.builder()
                .totalCount(totalCount)
                .currentMonthCount(currentMonthCount)
                .previousMonthCount(previousMonthCount)
                .growthRate(growthRate)
                .build();
    }

    // 일별 포스트 통계
    @Override
    public List<DailyPostStatisticsDTO> findDailyPostStatistics(LocalDate startDate, LocalDate endDate) {
        QPostEntity post = QPostEntity.postEntity;

        StringTemplate date = Expressions.stringTemplate("TO_CHAR({0}, 'YYYY-MM-DD')", post.createdAt);
        return jpaQueryFactory
                .select(Projections.constructor(DailyPostStatisticsDTO.class,
                        date,
                        post.id.count().as("postCount")
                ))
                .from(post)
                .where(post.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59))
                       .and(post.isDeleted.isFalse()))
                .groupBy(date)
                .orderBy(date.asc())
                .fetch();
    }
}
