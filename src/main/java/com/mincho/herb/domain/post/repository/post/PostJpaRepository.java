package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
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
                
        /* 상세 페이지 조회 부분 추가해야 함*/
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

        @Query("SELECT p.member FROM PostEntity p WHERE p.id = :postId AND p.member.email = :email")
        Optional<UserEntity> findAuthorByPostIdAndEmail(@Param("postId") Long postId, @Param("email") String email);
}
