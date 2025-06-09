package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.domain.Author;
import com.mincho.herb.domain.post.dto.*;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.QPostEntity;
import com.mincho.herb.domain.post.entity.QPostLikeEntity;
import com.mincho.herb.domain.post.entity.QPostViewsEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.page.PageInfoDTO;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Coalesce;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class PostRepositoryImpl implements PostRepository {
    private final PostJpaRepository postJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    // 자주 사용되는 QueryDSL 엔티티를 클래스 필드로 선언
    private final QPostEntity postEntity = QPostEntity.postEntity;
    private final QPostLikeEntity postLikeEntity = QPostLikeEntity.postLikeEntity;
    private final QPostViewsEntity postViewsEntity = QPostViewsEntity.postViewsEntity;

    @Override
    public PostEntity save(PostEntity postEntity) {
        return postJpaRepository.save(postEntity);
    }

    @Override
    public Object[][] findByPostId(Long postId) {
        return postJpaRepository.findByPostId(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public PostEntity findById(Long postId) {
        return postJpaRepository.findById(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않습니다."));
    }

    @Override
    public DetailPostDTO findDetailPostById(Long postId, String email) {
        QUserEntity userEntity = QUserEntity.userEntity;

        // 게시글 상세 정보 조회
        PostEntity post = postJpaRepository.findByIdWithTagsAndDetails(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않습니다."));

        // 게시글 조회수 조회
        Long viewCount = jpaQueryFactory
                .select(postViewsEntity.viewCount)
                .from(postViewsEntity)
                .where(postViewsEntity.post.id.eq(postId))
                .fetchOne();

        // null인 경우 0으로 처리
        if (viewCount == null) {
            viewCount = 0L;
        }

        // 좋아요 수
        Long likeCount = jpaQueryFactory
                .select(postLikeEntity.count())
                .from(postLikeEntity)
                .where(postLikeEntity.post.id.eq(postId))
                .fetchOne();

        // null인 경우 0으로 처리
        if (likeCount == null) {
            likeCount = 0L;
        }

        // 본인 여부 확인
        Boolean isMine = false;
        if (email != null) {
            UserEntity author = jpaQueryFactory
                    .select(userEntity)
                    .from(userEntity)
                    .where(userEntity.email.eq(email))
                    .fetchOne();

            if (author != null) {
                isMine = author.getId().equals(post.getUser().getId());
            }
        }

        // 작성자 정보
        Author author = Author.builder()
                .id(post.getUser().getId())
                .nickname(post.getUser().getProfile().getNickname())
                .profileImage(post.getUser().getProfile().getAvatarUrl())
                .build();

        // 응답 DTO 구성
        return DetailPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .author(author)
                .category(PostCategoryDTO.builder()
                        .id(post.getCategory().getId())
                        .name(post.getCategory().getName())
                        .type(post.getCategory().getType())
                        .description(post.getCategory().getDescription())
                        .build())
                .isMine(isMine)
                .likeCount(likeCount)
                .viewCount(viewCount)
                .createdAt(post.getCreatedAt())
                .tags(post.getTags()) // 태그 목록 포함
                .build();
    }

    @Override
    public PostResponseDTO findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO, String email) {
        QUserEntity userEntity = QUserEntity.userEntity;
        Long currentUserId = null;

        // 사용자가 로그인한 경우, 사용자 ID를 조회
        if (email != null && !email.isEmpty()) {
            UserEntity user = jpaQueryFactory
                    .select(userEntity)
                    .from(userEntity)
                    .where(userEntity.email.eq(email))
                    .fetchOne();

            if (user != null) {
                currentUserId = user.getId();
            }
        }

        // 검색 조건 생성
        BooleanBuilder builder = createSearchCondition(searchConditionDTO);

        // 정렬 조건 생성
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(searchConditionDTO);

        // 페이징 정보
        int page = pageInfoDTO.getPage();
        int size = pageInfoDTO.getSize();
        int offset = page * size;

        // 현재 시간 기준으로 24시간 이내의 시간 계산 (새 게시글 표시용)
        LocalDateTime yesterday = LocalDateTime.now().minusHours(24);

        // 조회 실행 - 조인을 사용하여 좋아요 수와 조회수 가져오는 쿼리 실행
        // 본인 글 여부도 함께 계산
        List<PostDTO> posts = jpaQueryFactory
                .select(Projections.constructor(PostDTO.class,
                        postEntity.id,
                        postEntity.title,
                        Projections.constructor(PostCategoryDTO.class,
                                postEntity.category.id,
                                postEntity.category.type,
                                postEntity.category.name,
                                postEntity.category.description
                        ),
                        Projections.constructor(Author.class,
                                postEntity.user.id,
                                postEntity.user.profile.nickname,
                                postEntity.user.profile.avatarUrl
                        ),
                        jpaQueryFactory.select(postLikeEntity.count())
                                .from(postLikeEntity)
                                .where(postLikeEntity.post.id.eq(postEntity.id)),
                        new Coalesce<Long>().add(postViewsEntity.viewCount).add(0L),
                        postEntity.createdAt,
                        postEntity.createdAt.after(yesterday), // 새 게시글 여부
                        currentUserId != null ? postEntity.user.id.eq(currentUserId) : Expressions.constant(false) // 본인 글 여부
                ))
                .from(postEntity)
                .leftJoin(postViewsEntity).on(postViewsEntity.post.id.eq(postEntity.id))
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(size)
                .fetch();

        // 총 게시글 수 조회 - 별도의 쿼리 실행을 최소화하기 위해 동일한 조건으로 카운트
        Long totalCount = jpaQueryFactory
                .select(postEntity.count())
                .from(postEntity)
                .where(builder)
                .fetchOne();

        // null 처리
        if (totalCount == null) {
            totalCount = 0L;
        }

        // PostResponseDTO로 결과 반환
        return PostResponseDTO.builder()
                .posts(posts)
                .totalCount(totalCount)
                .build();
    }

    @Override
    public Long countAllByConditions(SearchConditionDTO searchConditionDTO) {
        // 검색 조건 생성
        BooleanBuilder builder = createSearchCondition(searchConditionDTO);

        // 전체 게시글 수 조회
        return jpaQueryFactory
                .select(postEntity.count())
                .from(postEntity)
                .where(builder)
                .fetchOne();
    }

    @Override
    public PostEntity findByIdAndIsDeletedFalse(Long postId) {
        return postJpaRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않거나 삭제되었습니다."));
    }

    @Override
    public Long findAuthorIdByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorIdByPostIdAndEmail(postId, email)
                .orElseThrow(() -> createNotFoundException("해당 게시글의 작성자가 아닙니다."));
    }

    @Override
    public UserEntity findAuthorByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorByPostIdAndEmail(postId, email)
                .orElseThrow(() -> createNotFoundException("해당 게시글의 작성자가 아닙니다."));
    }

    @Override
    public void update(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    @Override
    public List<PostEntity> findAllByUser(UserEntity user) {
        return postJpaRepository.findAllByUser(user);
    }

    // 사용자 ID로 게시글 목록을 조회하고, 각 게시글의 조회수와 좋아요 수를 포함한 DTO를 반환
    @Override
    public UserPostResponseDTO findAllByUserId(Long userId, Pageable pageable) {
        Page<PostEntity> postPage = postJpaRepository.findByUserId(userId, pageable);

        // 결과를 DTO로 변환
        List<UserPostDTO> posts = postPage.getContent().stream()
                .map(postEntity -> {
                    // 각 게시글의 조회수 조회
                    Long viewCount = jpaQueryFactory
                            .select(postViewsEntity.viewCount)
                            .from(postViewsEntity)
                            .where(postViewsEntity.post.id.eq(postEntity.getId()))
                            .fetchOne();

                    // null인 경우 0으로 처리
                    if(viewCount == null) {
                        viewCount = 0L;
                    }

                    // 좋아요 수
                    Long likeCount = jpaQueryFactory
                            .select(postLikeEntity.count())
                            .from(postLikeEntity)
                            .where(postLikeEntity.post.id.eq(postEntity.getId()))
                            .fetchOne();

                    // null인 경우 0으로 처리
                    if(likeCount == null) {
                        likeCount = 0L;
                    }

                    return UserPostDTO.builder()
                            .id(postEntity.getId())
                            .title(postEntity.getTitle())
                            .contents(postEntity.getContents())
                            .createdAt(postEntity.getCreatedAt())
                            .category(PostCategoryDTO.builder()
                                    .id(postEntity.getCategory().getId())
                                    .name(postEntity.getCategory().getName())
                                    .type(postEntity.getCategory().getType())
                                    .description(postEntity.getCategory().getDescription())
                                    .build())
                            .likeCount(likeCount)
                            .viewCount(viewCount)
                            .tags(postEntity.getTags())
                            .build();
                })
                .toList();

        return UserPostResponseDTO.builder()
                .posts(posts)
                .totalCount(postPage.getTotalElements())
                .build();
    }

    @Override
    public Page<PostEntity> findByUserId(Long userId, Pageable pageable) {
        return postJpaRepository.findByUserId(userId, pageable);
    }

    /**
     * 태그 필터를 적용합니다.
     */
    private void applyTagFilter(BooleanBuilder builder, String tag) {
        if (tag != null && !tag.trim().isEmpty()) {
            String trimmedTag = tag.trim();
            // 게시글의 tags 컬렉션에 해당 태그가 포함되어 있는지 검사
            builder.and(postEntity.tags.contains(trimmedTag));
        }
    }

    /**
     * 검색 조건을 생성합니다.
     */
    private BooleanBuilder createSearchCondition(SearchConditionDTO searchConditionDTO) {
        BooleanBuilder builder = new BooleanBuilder();
        Long categoryId = searchConditionDTO.getCategoryId();
        String query = searchConditionDTO.getQuery();
        String queryType = searchConditionDTO.getQueryType();
        String tag = searchConditionDTO.getTag();  // 태그 검색 조건 추가

        // 카테고리 필터 적용
        applyCategoryFilter(builder, categoryId);

        // 삭제글 제외
        builder.and(postEntity.isDeleted.isFalse());

        // 검색어 필터 적용
        applySearchFilter(builder, query, queryType);

        // 태그 필터 적용
        applyTagFilter(builder, tag);

        return builder;
    }

    /**
     * 카테고리 필터를 적용합니다.
     */
    private void applyCategoryFilter(BooleanBuilder builder, Long categoryId) {
        if (categoryId != null && categoryId > 0) {
            builder.and(postEntity.category.id.eq(categoryId));
        }
    }

    /**
     * 검색어 필터를 적용합니다.
     */
    private void applySearchFilter(BooleanBuilder builder, String query, String queryType) {
        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = "%" + query.trim() + "%";

            if ("title".equalsIgnoreCase(queryType)) {
                builder.and(postEntity.title.like(trimmedQuery));
            } else if ("content".equalsIgnoreCase(queryType)) {
                builder.and(postEntity.contents.like(trimmedQuery));
            } else if ("author".equalsIgnoreCase(queryType)) {
                builder.and(postEntity.user.profile.nickname.like(trimmedQuery));
            } else {
                // 기본적으로 제목과 내용에서 검색
                builder.and(postEntity.title.like(trimmedQuery).or(postEntity.contents.like(trimmedQuery)));
            }
        }
    }

    /**
     * 정렬 조건을 생성합니다.
     */
    private OrderSpecifier<?> createOrderSpecifier(SearchConditionDTO searchConditionDTO) {
        String sort = searchConditionDTO.getSort();
        String order = searchConditionDTO.getOrder();
        boolean isAsc = "asc".equalsIgnoreCase(order);

        // 정렬 기준에 따라 다른 정렬 조건 반환
        if ("created_at".equalsIgnoreCase(sort)) {
            return isAsc ? postEntity.createdAt.asc() : postEntity.createdAt.desc();
        } else if ("title".equalsIgnoreCase(sort)) {
            return isAsc ? postEntity.title.asc() : postEntity.title.desc();
        } else if ("view_count".equalsIgnoreCase(sort)) {
            // 조회수 기준 정렬은 별도 처리 필요
            return isAsc ? postViewsEntity.viewCount.asc() : postViewsEntity.viewCount.desc();
        } else {
            // 기본값은 ID 기준 정렬
            return isAsc ? postEntity.id.asc() : postEntity.id.desc();
        }
    }
 /**
  * 태그 사용 빈도를 집계하여 반환합니다.
  *
  * @param limit 최대 반환 태그 수
  * @return 태그명과 사용 횟수의 리스트
  */
 @Override
 @SuppressWarnings("unchecked") // 네이티브 쿼리 결과 처리 시 발생하는 unchecked 경고 억제
 public List<TagCountDTO> findTagsWithCount(int limit) {
     // 태그별 사용 횟수를 계산하는 네이티브 쿼리
     String sql = """
             SELECT tag as name, COUNT(*) as count
             FROM post_tags pt
             JOIN post p ON pt.post_id = p.id
             WHERE p.is_deleted = FALSE
             GROUP BY tag
             ORDER BY count DESC
             LIMIT ?
             """;

     // 네이티브 쿼리 실행
     List<Object[]> results = entityManager
             .createNativeQuery(sql)
             .setParameter(1, limit)
             .getResultList();

     // 결과를 TagCountDTO로 변환
     return results.stream()
             .map(result -> TagCountDTO.builder()
                     .name((String) result[0])
                     .count(((Number) result[1]).longValue())
                     .build())
             .collect(java.util.stream.Collectors.toList());
 }
    /**
     * Not Found 예외를 생성합니다.
     */
    private CustomHttpException createNotFoundException(String message) {
        return new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, message);
    }
}
