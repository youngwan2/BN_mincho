package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentJpaRepository  extends JpaRepository<CommentEntity, Long> {


    @Query("SELECT c FROM Comments c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.profile " +
            "WHERE c.post.id = :postId AND c.parentComment.id IS NULL")
    List<CommentEntity> findByPostId(@Param("postId") Long postId);

    @Query("SELECT c FROM Comments c " +
            "LEFT JOIN FETCH c.member m " +
            "LEFT JOIN FETCH m.profile " +
            "WHERE c.parentComment = :parentComment")
    List<CommentEntity> findByParentComment(@Param("parentComment") CommentEntity parentComment);


    // 사용자 별 댓글 통계
    @Query("SELECT COUNT(c) FROM Comments c WHERE c.member.id = :memberId")
    Long countByMemberId(@Param("memberId") Long memberId);

    // 사용자 별 댓글
    @Query("SELECT c FROM Comments c WHERE c.member.id = :memberId")
    List<CommentEntity> findByMemberId(@Param("memberId") Long memberId);
}
