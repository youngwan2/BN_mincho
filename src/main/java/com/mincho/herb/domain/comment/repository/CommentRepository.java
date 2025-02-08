package com.mincho.herb.domain.comment.repository;


import com.mincho.herb.domain.comment.entity.CommentEntity;

public interface CommentRepository {

    void save(CommentEntity commentEntity);
    CommentEntity findById(Long parentCommentId);

}
