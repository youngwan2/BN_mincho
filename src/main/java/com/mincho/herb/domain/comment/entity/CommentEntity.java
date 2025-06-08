package com.mincho.herb.domain.comment.entity;

import com.mincho.herb.domain.comment.domain.Comment;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity(name = "Comments")
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"user", "post", "parentComment", "mentions"})
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name ="post_id")
    private PostEntity post;

    @ManyToOne // 스스로를 참조하므로 1:1 관계로 외래키 지정
    @JoinColumn(name = "parent_comment_id")
    private CommentEntity parentComment;
    private Long level;
    private Boolean deleted;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommentMentionEntity> mentions = new ArrayList<>();

    public static CommentEntity toEntity(Comment comment, UserEntity userEntity, CommentEntity parentComment, PostEntity postEntity){
        CommentEntity commentEntity = new CommentEntity();
                commentEntity.id = comment.getId();
                commentEntity.contents = comment.getContents();
                commentEntity.parentComment = parentComment;
                commentEntity.user = userEntity;
                commentEntity.post = postEntity;
                commentEntity.level = comment.getLevel();
                commentEntity.deleted = comment.getDeleted();
        return commentEntity;
    }

    public Comment toModel(){
        return Comment.builder()
                .id(this.id)
                .contents(this.contents)
                .level(this.level)
                .postId(this.post.getId())
                .deleted(this.deleted)
                .build();
    }

    // 멘션 추가 메서드
    public void addMention(CommentMentionEntity mention) {
        this.mentions.add(mention);
        mention.setComment(this);
    }
}
