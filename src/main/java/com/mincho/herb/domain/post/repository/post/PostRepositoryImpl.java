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
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository{
    private final PostJpaRepository postJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void save(PostEntity postEntity) {
        postJpaRepository.save(postEntity);
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

        BooleanBuilder builder = new BooleanBuilder();

        String category = searchConditionDTO.getCategory();
        String sort = searchConditionDTO.getSort();
        String order = searchConditionDTO.getOrder();
        String query = searchConditionDTO.getQuery();

        // 페이징 오프셋
        long offset = (long) (pageInfoDTO.getPage() * pageInfoDTO.getSize());

        // 카테고리 조건
        builder.and(postEntity.category.category.contains(searchConditionDTO.getCategory()));

        // 검색 조건
        if(searchConditionDTO.getQuery() != null && !searchConditionDTO.getQuery().isEmpty()){
        builder.and(postEntity.contents.contains(searchConditionDTO.getQuery()));
        }

        // 조건이 하나도 없으면 모든 목록 반환 아니면 필터링 조건에 맞게 처리
        return jpaQueryFactory
                .select(Projections.constructor(PostDTO.class,
                        postEntity.id,
                        postEntity.title,
                        postEntity.category.category,
                        postEntity.member.profile.nickname,
                        postLikeEntity.count().as("likeCount"),
                        postEntity.createdAt
                        ))
                .from(postEntity)
                .leftJoin(postLikeEntity).on(postLikeEntity.post.id.eq(postEntity.id))
                .fetchJoin()
                .where(builder)
                .groupBy(postEntity.id)
                .offset(offset)
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
