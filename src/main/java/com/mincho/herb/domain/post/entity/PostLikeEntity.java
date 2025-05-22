package com.mincho.herb.domain.post.entity;

import com.mincho.herb.domain.post.domain.PostLike;
import com.mincho.herb.domain.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PostEntity post;

    public static PostLikeEntity toEntity(Long id, UserEntity userEntity, PostEntity postEntity){
        PostLikeEntity postLikeEntity = new PostLikeEntity();
        postLikeEntity.id = id;
        postLikeEntity.post = postEntity;
        postLikeEntity.user = userEntity;
        return postLikeEntity;
    }

    public PostLike toModel(){
        return  PostLike.builder()
                .id(this.id)
                .postId(this.post.getId())
                .userId(this.user.getId())
                .build();
    }
}
