package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.dto.CommentDTO;
import com.mincho.herb.domain.comment.entity.CommentEntity;
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
    public void save(CommentEntity commentEntity) {
        commentJpaRepository.save(commentEntity);
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
        return commentJpaRepository.findByPostIdAndMemberId(postId, memberId);
    }

    @Override
    public List<CommentDTO> findByParentCommentIdAndMemberId(Long parentCommentId, Long memberId) {
        return commentJpaRepository.findByParentCommentIdAndMemberId(parentCommentId, memberId);
    }
    
    /** 마이페이지 */
    // 사용자별 댓글 개수
    @Override
    public Long countByMemberId(Long memberId) {
        return commentJpaRepository.countByMemberId(memberId);
    }

    // 사용자별 댓글 조회
    @Override
    public Page<CommentEntity> findByMemberId(Long memberId, Pageable pageable) {
        return commentJpaRepository.findByMemberId(memberId, pageable );
    }
}
