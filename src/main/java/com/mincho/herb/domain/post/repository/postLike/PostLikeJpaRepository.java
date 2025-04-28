package com.mincho.herb.domain.post.repository.postLike;

import com.mincho.herb.domain.post.entity.PostLikeEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeJpaRepository extends JpaRepository<PostLikeEntity, Long> {

    @Query("SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END FROM PostLikeEntity pl WHERE pl.member.id = :userId AND pl.post.id = :postId ")
    Boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.member.id = :userId AND pl.post.id = :postId")
    void deleteByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    @Query("SELECT COUNT(pl) FROM PostLikeEntity pl WHERE pl.id = :postId")
    Integer findLikeCountById(@Param("postId") Long postId);

    @Modifying
    @Query("DELETE FROM PostLikeEntity pl WHERE pl.member =:member")
    void deleteByMember(MemberEntity member);
}
