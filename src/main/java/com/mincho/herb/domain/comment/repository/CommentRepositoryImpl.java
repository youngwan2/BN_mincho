package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.entity.CommentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
}
