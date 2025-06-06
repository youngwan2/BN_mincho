package com.mincho.herb.domain.bookmark.repository.postBookmark;

import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkDTO;
import com.mincho.herb.domain.bookmark.dto.postBookmark.PostBookmarkResponseDTO;
import com.mincho.herb.domain.bookmark.entity.PostBookmarkEntity;
import com.mincho.herb.domain.bookmark.entity.QPostBookmarkEntity;
import com.mincho.herb.domain.post.entity.QPostEntity;
import com.mincho.herb.domain.user.entity.QUserEntity;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostBookmarkRepositoryImpl implements PostBookmarkRepository {

    private final PostBookmarkJpaRepository postBookmarkJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PostBookmarkEntity save(PostBookmarkEntity postBookmarkEntity) {
        return postBookmarkJpaRepository.save(postBookmarkEntity);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndPostId(Long userId, Long postId) {
        postBookmarkJpaRepository.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public boolean existsByUserIdAndPostId(Long userId, Long postId) {
        return postBookmarkJpaRepository.existsByUserIdAndPostId(userId, postId);
    }

    /**
     * 사용자가 북마크한 게시글 목록을 조회합니다.
     *
     * @param userId   사용자 ID
     * @param pageable 페이지 정보 (페이지 번호와 크기)
     * @return 북마크된 게시글 목록
     */
    @Override
    public PostBookmarkResponseDTO findBookmarkedPostsByUserId(Long userId, Pageable pageable) {
        QPostBookmarkEntity postBookmark = QPostBookmarkEntity.postBookmarkEntity;
        QPostEntity post = QPostEntity.postEntity;
        QUserEntity user = QUserEntity.userEntity;

        long offset = (long) pageable.getPageNumber() * pageable.getPageSize();
        long limit = pageable.getPageSize();

        List<PostBookmarkDTO> postBookmarks = jpaQueryFactory
                .select(Projections.constructor(PostBookmarkDTO.class,
                        post.id.as("postId"),
                        post.title,
                        post.category.name.as("categoryName"),
                        post.user.profile.nickname.as("authorName"),
                        postBookmark.createdAt.as("bookmarkedAt")
                ))
                .from(postBookmark)
                .join(postBookmark.post, post)
                .join(post.user, user)
                .where(postBookmark.user.id.eq(userId)
                      .and(post.isDeleted.isFalse()))
                .orderBy(postBookmark.createdAt.desc())
                .offset(offset)
                .limit(limit)
                .fetch();

        Long totalCount = this.countByUserId(userId);

        return PostBookmarkResponseDTO.builder()
                .bookmarks(postBookmarks)
                .totalCount(totalCount)
                .build();

    }

    /**
     * 사용자의 북마크 수를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 북마크 수
     */
    @Override
    public Long countByUserId(Long userId) {
        QPostEntity post = QPostEntity.postEntity;
        QPostBookmarkEntity postBookmark = QPostBookmarkEntity.postBookmarkEntity;
        return jpaQueryFactory
                .select(postBookmark.count())
                .from(postBookmark)
                .join(postBookmark.post, post)
                .where(postBookmark.user.id.eq(userId)
                        .and(post.isDeleted.isFalse()))
                .fetchOne();
    }
}
