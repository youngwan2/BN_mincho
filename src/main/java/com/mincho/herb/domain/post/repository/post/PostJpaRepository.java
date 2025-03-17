package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

        // 카테고리별 게시글 조회
        @Query("""
                   SELECT p,
                          (
                                SELECT COUNT(pl)
                                FROM PostLikeEntity pl
                                WHERE pl.post.id = p.id
                          ) AS likeCount
                    FROM PostEntity p
                    WHERE p.category.category = :category
                """)
        Page<Object[]> findAllByCategoryWithLikeCount(@Param("category") String category, Pageable pageable);
                

        @Query("""
                    SELECT p,
                          (
                                SELECT COUNT(pl)
                                FROM PostLikeEntity pl
                                WHERE pl.post.id = p.id
                          ) AS likeCount
                    FROM PostEntity p
                    WHERE p.id = :postId
                """)
        Optional<Object[][]> findByPostId(@Param("postId") Long postId);

        @Query("SELECT p.member.id FROM PostEntity p WHERE p.id = :postId AND p.member.email = :email")
        Optional<Long> findAuthorIdByPostIdAndEmail(@Param("postId") Long postId, @Param("email") String email);

        // 이메일과 게시글 ID 에 따른 유저ID 조회
        @Query("SELECT p.member FROM PostEntity p WHERE p.id = :postId AND p.member.email = :email")
        Optional<MemberEntity> findAuthorByPostIdAndEmail(@Param("postId") Long postId, @Param("email") String email);

        // 카테고리별 포스트 개수 조회
        @Query("SELECT COUNT(p) FROM PostEntity p WHERE p.category.id = :categoryId")
        int countByCategoryId(@Param("categoryId") Long categoryId);

        // 카테고리별 포스트 통계
        @Query("SELECT new com.mincho.herb.domain.post.dto.PostCountDTO(c.category, COUNT(p)) " +
                "FROM PostCategoryEntity c LEFT JOIN PostEntity p ON p.category = c " +
                "GROUP BY c.category")
        List<PostCountDTO> countPostsByCategory();
        // 사용자가 작성한 게시글의 수
        int countByMemberId(Long memberId);

}
