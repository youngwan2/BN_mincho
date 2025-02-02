package com.mincho.herb.domain.post.repository.post;

import com.mincho.herb.domain.post.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {
        @Query("SELECT p FROM PostEntity p JOIN FETCH p.member WHERE p.category.category = :category")
        Page<PostEntity> findAllByCategory(@Param("category") String category, Pageable pageable);

        @Query("SELECT p.member.id FROM PostEntity p WHERE p.id = :postId AND p.member.email = :email")
        Optional<Long> findAuthorIdByPostIdAndEmail(@Param("postId") Long postId, @Param("email") String email);
}
