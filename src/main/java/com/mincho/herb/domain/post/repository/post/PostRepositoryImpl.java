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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
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

    /**
     * ID로 게시글을 조회하되, 삭제된 게시글은 제외합니다.
     *
     * @param postId 조회할 게시글 ID
     * @return 삭제되지 않은 게시글 엔티티
     */
    @Override
    public PostEntity findByIdAndIsDeletedFalse(Long postId) {
        return postJpaRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않습니다."));
    }

    /**
     * 검색 조건에 맞는 게시글 목록을 조회합니다.
     *
     * @param searchConditionDTO 검색 조건 DTO
     * @param pageInfoDTO 페이지 정보 DTO
     * @param email 현재 사용자의 이메일 (없으면 null)
     * @return List<PostDTO> 게시글 목록
     */
    @Override
    public List<PostDTO> findAllByConditions(SearchConditionDTO searchConditionDTO, PageInfoDTO pageInfoDTO, String email) {
        log.info("검색 조건: {}", searchConditionDTO);

        // 검색 조건 설정
        BooleanBuilder builder = createSearchCondition(searchConditionDTO);

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier = createOrderSpecifier(searchConditionDTO);

        // 페이지네이션 설정
        long offset = (long) pageInfoDTO.getPage() * pageInfoDTO.getSize();
        long limit = pageInfoDTO.getSize();

        // 최근 게시물 표시를 위한 조건 (3일 이내)
        BooleanExpression newPost = createNewPostExpression();

        // 현재 사용자의 글인지 확인하는 조건
        BooleanExpression isMine = createIsMineExpression(email);

        // 게시글 조회 쿼리 실행
        return jpaQueryFactory
                .select(Projections.constructor(PostDTO.class,
                        postEntity.id,
                        postEntity.title,
                        Projections.constructor(PostCategoryDTO.class,
                            postEntity.category.id,
                            postEntity.category.type,
                            postEntity.category.name,
                            postEntity.category.description
                        ),
                        postEntity.user.profile.nickname,
                        Expressions.numberTemplate(Long.class, "coalesce({0}, 0)", postLikeEntity.count()).as("likeCount"),
                        Expressions.numberTemplate(Long.class, "coalesce({0}, 0)", postViewsEntity.viewCount).as("viewCount"),
                        postEntity.createdAt,
                        newPost.as("newPost"),
                        isMine.as("isMine")
                ))
                .from(postEntity)
                .leftJoin(postLikeEntity).on(postLikeEntity.post.id.eq(postEntity.id))
                .leftJoin(postViewsEntity).on(postViewsEntity.post.id.eq(postEntity.id))
                .fetchJoin()
                .where(builder)
                .groupBy(postEntity.id, postEntity.category, postEntity.user.profile.nickname, postViewsEntity.viewCount, postEntity.user.email)
                .orderBy(orderSpecifier)
                .offset(offset)
                .limit(limit)
                .fetch();
    }

    /**
     * 검색 조건에 맞는 총 게시글 수를 조회합니다.
     *
     * @param searchConditionDTO 검색 조건 DTO
     * @return 총 게시글 수
     */
    @Override
    public Long countAllByConditions(SearchConditionDTO searchConditionDTO) {
        // 검색 조건 설정
        BooleanBuilder builder = createSearchCondition(searchConditionDTO);

        // 총 게시글 수 조회
        return jpaQueryFactory
                .select(postEntity.count())
                .from(postEntity)
                .where(builder)
                .fetchOne();
    }

    /**
     * 현재 사용자의 글인지 확인하는 표현식을 생성합니다.
     *
     * @param email 현재 사용자의 이메일 (null일 수 있음)
     * @return 현재 사용자 소유의 글인지를 나타내는 BooleanExpression
     */
    private BooleanExpression createIsMineExpression(String email) {
        // 이메일이 null이거나 비어있으면 (로그인하지 않은 경우) 항상 false 반환
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return Expressions.FALSE;
        }

        // 로그인한 경우 현재 사용자와 게시글 작성자 이메일 비교
        return postEntity.user.email.eq(email);
    }

    /**
     * 3일 이내에 작성된 게시글을 구분하는 표현식을 생성합니다.
     */
    private BooleanExpression createNewPostExpression() {
        return new CaseBuilder()
                .when(postEntity.createdAt.goe(LocalDateTime.now().minusDays(3)))
                .then(true)
                .otherwise(false);
    }

    /**
     * 검색 조건을 생성합니다.
     */
    private BooleanBuilder createSearchCondition(SearchConditionDTO searchConditionDTO) {
        BooleanBuilder builder = new BooleanBuilder();
        Long categoryId = searchConditionDTO.getCategoryId();
        String query = searchConditionDTO.getQuery();
        String queryType = searchConditionDTO.getQueryType();

        // 카테고리 필터 적용
        applyCategoryFilter(builder, categoryId);

        // 삭제글 제외
        builder.and(postEntity.isDeleted.isFalse());

        // 검색어 필터 적용
        applySearchFilter(builder, query, queryType);

        return builder;
    }

    /**
     * 카테고리 필터를 적용합니다.
     */
    private void applyCategoryFilter(BooleanBuilder builder, Long categoryId) {
        if (categoryId != null) {
            if (categoryId > 0) {
                // 특정 카테고리 조회
                builder.and(postEntity.category.id.eq(categoryId));
            } else if (categoryId == 0) {
                // 모든 카테고리 조회
                builder.and(postEntity.category.id.isNotNull());
            }
        }
    }

    /**
     * 검색어 필터를 적용합니다.
     */
    private void applySearchFilter(BooleanBuilder builder, String query, String queryType) {
        if (query != null && !query.trim().isEmpty()) {
            String trimmedQuery = query.trim();

            switch (queryType) {
                case "title": // 제목 기준 검색
                    builder.and(postEntity.title.containsIgnoreCase(trimmedQuery));
                    break;
                case "content": // 내용 기준 검색
                    builder.and(postEntity.contents.containsIgnoreCase(trimmedQuery));
                    break;
                case "author": // 저자 기준 검색
                    builder.and(postEntity.user.profile.nickname.containsIgnoreCase(trimmedQuery));
                    break;
                case "all": // 전체 대상 검색
                default:
                    builder.and(
                            postEntity.title.containsIgnoreCase(trimmedQuery)
                                    .or(postEntity.contents.containsIgnoreCase(trimmedQuery))
                                    .or(postEntity.user.profile.nickname.containsIgnoreCase(trimmedQuery))
                    );
                    break;
            }
        }
    }

    /**
     * 정렬 조건을 생성합니다.
     */
    private OrderSpecifier<?> createOrderSpecifier(SearchConditionDTO searchConditionDTO) {
        String sort = searchConditionDTO.getSort(); // 정렬 기준
        String order = searchConditionDTO.getOrder(); // 정렬 방향
        boolean isAscending = "asc".equalsIgnoreCase(order);

        // 정렬 기준에 따라 OrderSpecifier 생성
        if ("post_id".equals(sort)) { // 포스트 ID 기준
            return isAscending ? postEntity.id.asc() : postEntity.id.desc();
        } else if ("created_at".equals(sort)) { // 작성일 기준
            return isAscending ? postEntity.createdAt.asc() : postEntity.createdAt.desc();
        } else if ("like_count".equals(sort)) { // 좋아요 수 기준
            return isAscending ? postLikeEntity.count().asc() : postLikeEntity.count().desc();
        }

        // 기본값: 포스트 ID 기준 내림차순
        return postEntity.id.desc();
    }

    /**
     * 게시글 작성자 ID를 이메일과 함께 조회합니다.
     */
    @Override
    public Long findAuthorIdByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorIdByPostIdAndEmail(postId, email)
                .orElseThrow(() -> createForbiddenException("요청 권한이 있는 유저가 아닙니다."));
    }

    /**
     * 게시글 작성자를 이메일과 함께 조회합니다.
     */
    @Override
    public UserEntity findAuthorByPostIdAndEmail(Long postId, String email) {
        return postJpaRepository.findAuthorByPostIdAndEmail(postId, email)
                .orElseThrow(() -> createForbiddenException("요청 권한이 있는 유저가 아닙니다."));
    }

    /**
     * 게시글 엔티티를 업데이트합니다.
     */
    @Override
    public void update(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
    }

    /**
     * 특정 사용자의 모든 게시글을 조회합니다.
     */
    @Override
    public List<PostEntity> findAllByUser(UserEntity user) {
        return postJpaRepository.findAllByUser(user);
    }

    /**
     * 사용자별 게시글 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return UserPostResponseDTO
     */
    @Override
    public UserPostResponseDTO findAllByUserId(Long userId, Pageable pageable) {
        // 사용자의 게시글 목록 조회
        List<UserPost> posts = findUserPosts(userId, pageable);

        // 사용자의 총 게시글 수 조회
        Long totalCount = countUserPosts(userId);

        return UserPostResponseDTO.builder()
                .posts(posts)
                .totalCount(totalCount)
                .build();
    }

    /**
     * 사용자의 게시글 목록을 조회합니다.
     */
    private List<UserPost> findUserPosts(Long userId, Pageable pageable) {
        return jpaQueryFactory
                .select(Projections.constructor(UserPost.class,
                        postEntity.id,
                        postEntity.title,
                        Projections.constructor(PostCategoryDTO.class,
                                postEntity.category.id,
                                postEntity.category.type,
                                postEntity.category.name,
                                postEntity.category.description
                        ),
                        postEntity.createdAt
                ))
                .from(postEntity)
                .where(postEntity.user.id.eq(userId)
                        .and(postEntity.isDeleted.isFalse()))
                .orderBy(postEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    /**
     * 사용자의 총 게시글 수를 조회합니다.
     */
    private Long countUserPosts(Long userId) {
        return jpaQueryFactory
                .select(postEntity.count())
                .from(postEntity)
                .where(postEntity.user.id.eq(userId)
                        .and(postEntity.isDeleted.isFalse()))
                .fetchOne();
    }

    /**
     * 사용자별 게시글 목록을 페이징하여 조회합니다.
     */
    @Override
    public Page<PostEntity> findByUserId(Long userId, Pageable pageable) {
        return postJpaRepository.findByUserId(userId, pageable);
    }

    /**
     * 게시글을 찾을 수 없을 때 발생시킬 예외를 생성합니다.
     */
    private CustomHttpException createNotFoundException(String message) {
        return new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, message);
    }

    /**
     * 게시글에 접근 권한이 없을 때 발생시킬 예외를 생성합니다.
     */
    private CustomHttpException createForbiddenException(String message) {
        return new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, message);
    }

    /**
     * QueryDSL을 사용하여 게시글 상세 정보와 태그를 함께 조회합니다.
     *
     * @param postId 게시글 ID
     * @param email 현재 사용자의 이메일 (없으면 null)
     * @return DetailPostDTO 게시글 상세 정보 DTO
     */
    @Override
    public DetailPostDTO findDetailPostById(Long postId, String email) {
        QPostEntity qPostEntity = postEntity;
        QPostViewsEntity qPostViewsEntity = postViewsEntity;
        QPostLikeEntity qPostLikeEntity = postLikeEntity;

        // 게시글 조회 - 이제 EntityManager를 직접 사용해서 태그를 함께 로딩합니다
        PostEntity post = postJpaRepository.findByIdWithTagsAndDetails(postId)
                .orElseThrow(() -> createNotFoundException("해당 게시글은 존재하지 않습니다."));

        // 게시글이 삭제되었는지 확인
        if (post.getIsDeleted()) {
            throw createNotFoundException("삭제된 게시글입니다.");
        }

        // 조회수 정보
        Long viewCount = jpaQueryFactory
                .select(qPostViewsEntity.viewCount)
                .from(qPostViewsEntity)
                .where(qPostViewsEntity.post.id.eq(postId))
                .fetchOne();

        if (viewCount == null) {
            viewCount = 0L;
        }

        // 좋아요 수 조회
        Long likeCount = jpaQueryFactory
                .select(qPostLikeEntity.count())
                .from(qPostLikeEntity)
                .where(qPostLikeEntity.post.id.eq(postId))
                .fetchOne();

        if (likeCount == null) {
            likeCount = 0L;
        }

        // 이메일을 기반으로 isMine 여부 확인
        boolean isMine = false;
        if (email != null && !email.isEmpty()) {
            isMine = post.getUser().getEmail().equals(email);
        }

        // 작성자 정보 구성
        Author author = Author.builder()
                .id(post.getUser().getId())
                .nickname(post.getUser().getProfile().getNickname())
                .profileImage(post.getUser().getProfile().getAvatarUrl())
                .build();

        // 카테고리 정보 구성
        PostCategoryDTO category = PostCategoryDTO.builder()
                .id(post.getCategory().getId())
                .type(post.getCategory().getType())
                .name(post.getCategory().getName())
                .description(post.getCategory().getDescription())
                .build();

        // 태그 목록 (null 체크 추가)
        List<String> tags = post.getTags() != null ? post.getTags() : List.of();

        // 게시글 상세 정보 DTO 구성
        return DetailPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .contents(post.getContents())
                .author(author)
                .category(category)
                .isMine(isMine)
                .likeCount(likeCount)
                .viewCount(viewCount)
                .createdAt(post.getCreatedAt())
                .tags(tags) // null이면 빈 목록으로 대체
                .build();
    }
}
