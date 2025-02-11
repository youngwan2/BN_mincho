package com.mincho.herb.domain.comment.repository;


import com.mincho.herb.domain.comment.entity.CommentEntity;

import java.util.List;

public interface CommentRepository {

    void save(CommentEntity commentEntity);
    CommentEntity findById(Long parentCommentId);
    List<CommentEntity> findByPostId(Long postId);
    List<CommentEntity> findByParentComment(CommentEntity parentComment);

    void updateComment(CommentEntity commentEntity);


}
