package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.dto.CommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository{

    private final CommentJpaRepository commentJpaRepository;

    @Override
    public CommentEntity save(CommentEntity commentEntity) {
        commentJpaRepository.save(commentEntity);
        return commentEntity;
    }

    @Override
    public CommentEntity findById(Long parentCommentId) {
        Optional<CommentEntity> optional = commentJpaRepository.findById(parentCommentId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    @Override
    public List<CommentEntity> findByPostId(Long postId) {
        return commentJpaRepository.findByPostId(postId);

    }

    @Override
    public List<CommentEntity> findByParentComment(CommentEntity parentComment) {
        return commentJpaRepository.findByParentComment(parentComment);
    }

    @Override
    public void updateComment(CommentEntity commentEntity) {
        commentJpaRepository.save(commentEntity);
    }

    // 게시글별 댓글 개수
    @Override
    public Long countByPostId(Long postId) {
        return commentJpaRepository.countByPostId(postId);
    }


    /* 임시 쿼리 */
    @Override
    public List<CommentDTO> findByPostIdAndMemberId(Long postId, Long memberId) {
        return commentJpaRepository.findByPostIdAndUserId(postId, memberId);
    }

    @Override
    public List<CommentDTO> findByParentCommentIdAndUserId(Long parentCommentId, Long memberId) {
        return commentJpaRepository.findByParentCommentIdAndUserId(parentCommentId, memberId);
    }

    // 유저의 모든 댓글 목록 조회
    @Override
    public List<CommentEntity> findAllByUser(UserEntity user) {
        return commentJpaRepository.findAllByUser(user);
    }

    /** 마이페이지 */
    // 사용자별 댓글 개수
    @Override
    public Long countByMemberId(Long memberId) {
        return commentJpaRepository.countByUserId(memberId);
    }

    // 사용자별 댓글 조회
    @Override
    public Page<CommentEntity> findByMemberId(Long memberId, Pageable pageable) {
        return commentJpaRepository.findByUserId(memberId, pageable);
    }
}