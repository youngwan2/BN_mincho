package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.dto.CommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // 게시글 별 댓글 통계
    @Query("SELECT COUNT(c) FROM Comments c WHERE c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // 사용자 별 댓글 통계
    @Query("SELECT COUNT(c) FROM Comments c WHERE c.member.id = :memberId")
    Long countByMemberId(@Param("memberId") Long memberId);

    // 사용자 별 댓글
    @Query("SELECT c FROM Comments c WHERE c.member.id = :memberId")
    Page<CommentEntity> findByMemberId(@Param("memberId") Long memberId, Pageable pageable);


    // 해당 댓글이 유저가 작성한 글인지 체크한 항목을 포함하는 댓글 조회
    @Query("SELECT new com.mincho.herb.domain.comment.dto.CommentDTO(" +
            "c.id, " +
            "CASE WHEN c.deleted THEN '사용자에 의해 삭제된 댓글입니다.' ELSE c.contents END, " +
            "CASE WHEN c.deleted THEN '알 수 없음' ELSE c.member.profile.nickname END, " +
            "c.deleted, " +
            "c.parentComment.id, " +
            "c.level, " +
            "CASE WHEN c.member.id = :memberId THEN true ELSE false END, " +
            "c.createdAt, " +
            "c.updatedAt ) " +
            "FROM Comments c " +
            "WHERE c.post.id = :postId AND c.parentComment.id IS NULL " +
            "ORDER BY c.deleted asc ")
    List<CommentDTO> findByPostIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);

    // 부모 댓글 id 와 일치하는 경우 조회
    @Query("SELECT new com.mincho.herb.domain.comment.dto.CommentDTO(" +
            "c.id, " +
            "CASE WHEN c.deleted THEN '사용자에 의해 삭제된 댓글입니다.' ELSE c.contents END, " +
            "CASE WHEN c.deleted THEN '알 수 없음' ELSE c.member.profile.nickname END, " +
            "c.deleted, " +
            "c.parentComment.id, " +
            "c.level, " +
            "CASE WHEN c.member.id = :memberId THEN true ELSE false END, " +
            "c.createdAt, " +
            "c.updatedAt ) " +
            "FROM Comments c " +
            "WHERE c.parentComment.id = :parentCommentId " +
            "ORDER BY c.deleted asc")
    List<CommentDTO> findByParentCommentIdAndMemberId(@Param("parentCommentId") Long parentCommentId, @Param("memberId") Long memberId);

    // 유저의 모든 댓글 조회
    @Query("SELECT c FROM Comments c WHERE c.member = :member")
    List<CommentEntity> findAllByMember(MemberEntity member);
}
