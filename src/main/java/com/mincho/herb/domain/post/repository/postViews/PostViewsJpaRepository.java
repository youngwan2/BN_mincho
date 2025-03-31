package com.mincho.herb.domain.post.repository.postViews;

import com.mincho.herb.domain.post.entity.PostViewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostViewsJpaRepository extends JpaRepository<PostViewsEntity, Long> {

    // 조회수 증가
    @Query("UPDATE PostViewsEntity pv SET pv.viewCount = :newViewCount WHERE pv.post.id = :postId")
    int updatePostViewCount(@Param("newViewCount") Long newViewCount, @Param("postId") Long postId);

    // 조회수 조회
    Long findByPostId(Long postId);
}
