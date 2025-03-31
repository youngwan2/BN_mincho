package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.dto.PageInfoDTO;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.dto.PostDTO;
import com.mincho.herb.domain.post.dto.SearchConditionDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.post.entity.QPostEntity;
import com.mincho.herb.domain.post.entity.QPostLikeEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.domain.user.entity.QMemberEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

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
        QMemberEntity memberEntity = QMemberEntity.memberEntity;

        log.info("검색 조건: {}", searchConditionDTO);

        BooleanBuilder builder = new BooleanBuilder();
        String category = searchConditionDTO.getCategory();
        String sort = searchConditionDTO.getSort();
        String order = searchConditionDTO.getOrder();
        String query = searchConditionDTO.getQuery();

        // 페이징 오프셋 및 제한
        long offset = (long) (pageInfoDTO.getPage() * pageInfoDTO.getSize());
        long limit = pageInfoDTO.getSize();

        // 카테고리 필터 (정확한 매칭)
        if (category != null && !category.trim().isEmpty()) {
            builder.and(postEntity.category.category.eq(category));
        }

        // 검색 조건 (내용 포함 여부)
        if (query != null && !query.trim().isEmpty()) {
            builder.and(postEntity.contents.like("%" + query.trim() + "%"));
        }

        // 정렬 조건 설정
        OrderSpecifier<?> orderSpecifier;
        if ("desc".equalsIgnoreCase(order)) {
            orderSpecifier = postEntity.id.desc(); // 기본값: id 내림차순
        } else {
            orderSpecifier = postEntity.id.asc(); // 오름차순
        }

        return jpaQueryFactory
                .select(Projections.constructor(PostDTO.class,
                        postEntity.id, // 포스트 ID
                        postEntity.title, // 제목
                        postEntity.category.category, // 카테고리
                        postEntity.member.profile.nickname, // 사용자 닉네임
                        Expressions.numberTemplate(Long.class, "coalesce({0}, 0)", postLikeEntity.count()).as("likeCount"), // 좋아요 개수 (null 방지)
                        postEntity.createdAt // 생성 날짜
                ))
                .from(postEntity)
                .leftJoin(postLikeEntity).on(postLikeEntity.post.id.eq(postEntity.id))
                .fetchJoin()
                .where(builder)
                .groupBy(postEntity.id, postEntity.category, postEntity.member.profile.nickname)
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
    public MemberEntity findAuthorByPostIdAndEmail(Long postId, String email) {
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
    public int countByCategory(String category) {
        return 0;
    }

    // 사용자 별 게시글 수
    @Override
    public int countByMemberId(Long memberId) {
        int count = postJpaRepository.countByMemberId(memberId);
        return count;
    }

    // 카테고리 별 게시글 수 통계
    @Override
    public List<PostCountDTO> countsByCategory() {
        return postJpaRepository.countPostsByCategory();
    }
}
