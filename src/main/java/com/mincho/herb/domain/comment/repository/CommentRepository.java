package com.mincho.herb.domain.comment.repository;


import com.mincho.herb.domain.comment.dto.CommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository {

    CommentEntity save(CommentEntity commentEntity);
    CommentEntity findById(Long parentCommentId);
    List<CommentEntity> findByPostId(Long postId);
    List<CommentEntity> findByParentComment(CommentEntity parentComment);

    void updateComment(CommentEntity commentEntity);

    // 게시글 별 댓글 개수
    Long countByPostId(Long postId);
    
    // 사용자별 댓글 개수
    Long countByMemberId(Long memberId);

    // 사용자 별 댓글 조회
    Page<CommentEntity> findByMemberId(Long memberId, Pageable pageable);


    // 댓글 조회
    List<CommentDTO> findByPostId(Long postId, Long memberId);

    // 부모 댓글로 댓글 엔티티 조회
    List<CommentDTO> findByParentCommentId(Long parentCommentId, Long userId);

    // 유저가 작성한 모든 댓글 가져오기
    List<CommentEntity> findAllByUser(UserEntity member);


}
