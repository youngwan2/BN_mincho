package com.mincho.herb.domain.comment.repository;

import com.mincho.herb.domain.comment.entity.CommentEntity;
import com.mincho.herb.domain.comment.entity.CommentMentionEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMentionEntity, Long> {
    List<CommentMentionEntity> findByComment(CommentEntity comment);
    List<CommentMentionEntity> findByMentionedUser(UserEntity mentionedUser);
}
