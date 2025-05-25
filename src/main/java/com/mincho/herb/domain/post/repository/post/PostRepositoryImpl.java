package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.QPostEntity;
import com.mincho.herb.domain.post.entity.QPostLikeEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.page.PageInfoDTO;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.MathUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepository{
    private final PostJpaRepository postJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postJpaRepository.save(postEntity);
    }

    @Override
    public Object[][] findByPostId(Long postId) {
        return postJpaRepository.findByPostId(postId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public PostEntity findById(Long postId) {
        return postJpaRepository.findById(postId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "해당 게시글은 존재하지 않습니다."));
    }

    // 조건(검색, 정렬기준 등)
    @Override
    public List<PostDTO> findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO) {
        QPostEntity postEntity = QPostEntity.postEntity;
        QPostLikeEntity postLikeEntity = QPostLikeEntity.postLikeEntity;
        QUserEntity userEntity = QUserEntity.userEntity;

        log.info("검색 조건: {}", searchConditionDTO);

        BooleanBuilder builder = new BooleanBuilder();
        String category = searchConditionDTO.getCategory();
        String sort = searchConditionDTO.getSort(); // 정렬 방향 desc, asc
        String order = searchConditionDTO.getOrder(); // 정렬 기준 post_id 등
        String query = searchConditionDTO.getQuery(); // 검색어
        String queryType = searchConditionDTO.getQueryType(); // 검색 타입(title, contents, author 로 나눠서 검색 대상 필터링

        long offset = (long) pageInfoDTO.getPage() * pageInfoDTO.getSize();
        long limit = pageInfoDTO.getSize();

        // 카테고리 필터
        if (category != null && !category.trim().isEmpty() && !"all".equals(category)) {
            builder.and(postEntity.category.category.eq(category));
        }

        // 검색 조건
        if (query != null && !query.trim().isEmpty()) {
            switch (queryType) {
                case "title": // 제목 기준 검색
                    builder.and(postEntity.title.containsIgnoreCase(query.trim()));
                    break;
                case "content": // 내용 기준 검색
                    builder.and(postEntity.contents.containsIgnoreCase(query.trim()));
                    break;
                case "author": // 저자 기준 검색
                    builder.and(postEntity.user.profile.nickname.containsIgnoreCase(query.trim()));
                    break;
                case "all": // 전체 대상 검색
                default:
                    builder.and(
                            postEntity.title.containsIgnoreCase(query.trim())
                                    .or(postEntity.contents.containsIgnoreCase(query.trim()))
                                    .or(postEntity.user.profile.nickname.containsIgnoreCase(query.trim()))
                    );
                    break;
            }
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = "desc".equalsIgnoreCase(sort)
                ? postEntity.id.desc()
                : postEntity.id.asc();

        return jpaQueryFactory
                .select(Projections.constructor(PostDTO.class,
                        postEntity.id,
                        postEntity.title,
                        postEntity.category.category,
                        postEntity.user.profile.nickname,
                        Expressions.numberTemplate(Long.class, "coalesce({0}, 0)", postLikeEntity.count()).as("likeCount"),
                        postEntity.createdAt
                ))
                .from(postEntity)
                .leftJoin(postLikeEntity).on(postLikeEntity.post.id.eq(postEntity.id))
                .fetchJoin()
                .where(builder)
                .groupBy(postEntity.id, postEntity.category, postEntity.user.profile.nickname)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }
    // 해당 포스트를 작성한 유저 조회
    @Override
    public Long findAuthorIdByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorIdByPostIdAndEmail(postId, email)
                .orElseThrow(()-> new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 있는 유저가 아닙니다."));
    }


    @Override
    public UserEntity findAuthorByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorByPostIdAndEmail(postId, email)
                .orElseThrow(()-> new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "요청 권한이 있는 유저가 아닙니다."));
    }

    @Override
    public void update(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    @Override
    public void deleteById(Long id) {
        postJpaRepository.deleteById(id);
    }

    // 카테고리 별 게시글 개수
    @Override
    public Long countByCategory(String category) {
        return 0L;
    }



    // 카테고리 별 게시글 수 통계
    @Override
    public List<PostCountDTO> countsByCategory() {
        return postJpaRepository.countPostsByCategory();
    }

    @Override
    public List<PostEntity> findAllByUser(UserEntity user) {

        return postJpaRepository.findAllByUser(user);
    }

    // 사용자 별 게시글 수
    @Override
    public Long countByUserId(Long userId) {
        return  postJpaRepository.countByUserId(userId);
    }

    // 사용자별 게시글 목록
    @Override
    public Page<PostEntity> findByUserId(Long userId, Pageable pageable) {
        return postJpaRepository.findByUserId(userId, pageable);
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

        // 전체 포스트 개수
        Long totalCount = jpaQueryFactory.select(post.count()).from(post).fetchOne();

        // 이번 달의 포스트 개수
        Long currentMonthCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.createdAt.goe(startOfCurrentMonth))
                .fetchOne();

        // 저번 달의 포스트 개수
        Long previousMonthCount = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(post.createdAt.between(startOfPreviousMonth, endOfPreviousMonth))
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
                .where(post.createdAt.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
                .groupBy(date)
                .orderBy(date.asc())
                .fetch();
    }
}
