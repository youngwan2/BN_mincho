package com.mincho.herb.domain.comment.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.comment.domain.Comment;
import com.mincho.herb.domain.post.entity.PostEntity;
import com.mincho.herb.domain.user.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity(name = "Comments")
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CommentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String contents;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name ="post_id")
    private PostEntity post;

    @ManyToOne // 스스로를 참조하므로 1:1 관계로 외래키 지정
    @JoinColumn(name = "parent_comment_id")
    private CommentEntity parentComment;
    private Long level;
    private Boolean deleted;

    public static CommentEntity toEntity(Comment comment, MemberEntity memberEntity, CommentEntity parentComment, PostEntity postEntity){
        CommentEntity commentEntity = new CommentEntity();
                commentEntity.id = comment.getId();
                commentEntity.contents = comment.getContents();
                commentEntity.parentComment = parentComment;
                commentEntity.member = memberEntity;
                commentEntity.post = postEntity;
                commentEntity.level = comment.getLevel();
                commentEntity.deleted = comment.getDeleted();
        return commentEntity;
    }

    public Comment toModel(){
        return Comment.builder()
                .id(this.id)
                .contents(this.contents)
                .member(this.member.toModel())
                .post(this.post.toModel())
                .parentComment(this.parentComment.toModel())
                .level(this.level)
                .deleted(this.deleted)
                .build();

    }
}
