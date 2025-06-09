package com.mincho.herb.domain.comment.entity;

import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "CommentMentions")
@Table(name = "comment_mentions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"comment", "mentionedUser"})
public class CommentMentionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @ManyToOne
    @JoinColumn(name = "mentioned_user_id")
    private UserEntity mentionedUser;
}
