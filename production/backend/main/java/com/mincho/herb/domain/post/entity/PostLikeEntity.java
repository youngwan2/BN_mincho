package com.mincho.herb.domain.post.entity;

import com.mincho.herb.domain.post.domain.PostLike;
import com.mincho.herb.domain.user.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "PostLike", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id"})})
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PostLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private PostEntity post;

    public static PostLikeEntity toEntity(Long id, MemberEntity memberEntity, PostEntity postEntity){
        PostLikeEntity postLikeEntity = new PostLikeEntity();
        postLikeEntity.id = id;
        postLikeEntity.post = postEntity;
        postLikeEntity.member = memberEntity;
        return postLikeEntity;
    }

    public PostLike toModel(){
        return  PostLike.builder()
                .id(this.id)
                .postId(this.post.getId())
                .userId(this.member.getId())
                .build();
    }
}
