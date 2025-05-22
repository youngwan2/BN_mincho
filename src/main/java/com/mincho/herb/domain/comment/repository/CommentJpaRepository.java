package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.dto.CommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 댓글(CommentEntity)에 대한 JPA Repository.
 * 댓글 조회, 통계, 사용자 관련 쿼리를 포함함.
 */
public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

    /**
     * 특정 게시글에 달린 최상위(부모가 없는) 댓글들을 조회합니다.
     * 사용자 정보와 프로필을 함께 페치 조인합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 리스트
     */
    @Query("SELECT c FROM Comments c " +
            "LEFT JOIN FETCH c.user m " +
            "LEFT JOIN FETCH m.profile " +
            "WHERE c.post.id = :postId AND c.parentComment.id IS NULL")
    List<CommentEntity> findByPostId(@Param("postId") Long postId);

    /**
     * 특정 부모 댓글의 자식 댓글들을 조회합니다.
     * 사용자 정보와 프로필을 함께 페치 조인합니다.
     *
     * @param parentComment 부모 댓글 엔티티
     * @return 자식 댓글 리스트
     */
    @Query("SELECT c FROM Comments c " +
            "LEFT JOIN FETCH c.user m " +
            "LEFT JOIN FETCH m.profile " +
            "WHERE c.parentComment = :parentComment")
    List<CommentEntity> findByParentComment(@Param("parentComment") CommentEntity parentComment);

    /**
     * 특정 게시글에 작성된 전체 댓글 수를 반환합니다.
     *
     * @param postId 게시글 ID
     * @return 댓글 수
     */
    @Query("SELECT COUNT(c) FROM Comments c WHERE c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    /**
     * 특정 사용자가 작성한 전체 댓글 수를 반환합니다.
     *
     * @param userId 사용자 ID
     * @return 댓글 수
     */
    @Query("SELECT COUNT(c) FROM Comments c WHERE c.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 특정 사용자가 작성한 댓글 목록을 페이징하여 조회합니다.
     *
     * @param userId 사용자 ID
     * @param pageable 페이지 정보
     * @return 페이징된 댓글 목록
     */
    @Query("SELECT c FROM Comments c WHERE c.user.id = :userId")
    Page<CommentEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * 특정 게시글에 달린 최상위 댓글들을 조회하면서,
     * 각 댓글이 해당 유저가 작성한 것인지 여부를 포함한 DTO로 반환합니다.
     * 삭제되지 않은 댓글만 조회하며, ID 기준 내림차순 정렬합니다.
     *
     * @param postId   게시글 ID
     * @param userId 현재 로그인한 사용자 ID
     * @return 댓글 DTO 리스트
     */
    @Query("SELECT new com.mincho.herb.domain.comment.dto.CommentDTO(" +
            "c.id, " +
            "c.contents," +
            "c.user.profile.nickname," +
            "c.deleted, " +
            "c.parentComment.id, " +
            "c.level, " +
            "CASE WHEN c.user.id = :userId THEN true ELSE false END, " +
            "c.createdAt, " +
            "c.updatedAt ) " +
            "FROM Comments c " +
            "WHERE c.post.id = :postId AND c.parentComment.id IS NULL AND c.deleted = false " +
            "ORDER BY c.deleted ASC, c.id DESC")
    List<CommentDTO> findByPostIdAndUserId(@Param("postId") Long postId, @Param("userId") Long userId);

    /**
     * 특정 부모 댓글에 대한 자식 댓글들을 조회하면서,
     * 각 댓글이 해당 유저가 작성한 것인지 여부를 포함한 DTO로 반환합니다.
     *
     * @param parentCommentId 부모 댓글 ID
     * @param userId        현재 로그인한 사용자 ID
     * @return 댓글 DTO 리스트
     */
    @Query("SELECT new com.mincho.herb.domain.comment.dto.CommentDTO(" +
            "c.id, " +
            "c.contents," +
            "c.user.profile.nickname," +
            "c.deleted, " +
            "c.parentComment.id, " +
            "c.level, " +
            "CASE WHEN c.user.id = :userId THEN true ELSE false END, " +
            "c.createdAt, " +
            "c.updatedAt ) " +
            "FROM Comments c " +
            "WHERE c.parentComment.id = :parentCommentId AND c.deleted = false " +
            "ORDER BY c.deleted DESC, c.id DESC")
    List<CommentDTO> findByParentCommentIdAndUserId(@Param("parentCommentId") Long parentCommentId, @Param("userId") Long userId);

    /**
     * 특정 사용자가 작성한 모든 댓글을 조회합니다.
     *
     * @param user 사용자 엔티티
     * @return 댓글 리스트
     */
    @Query("SELECT c FROM Comments c WHERE c.user = :user")
    List<CommentEntity> findAllByUser(UserEntity user);
}
