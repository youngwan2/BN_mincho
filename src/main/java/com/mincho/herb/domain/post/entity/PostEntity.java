package com.mincho.herb.domain.post.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.post.domain.PostCategory;
import com.mincho.herb.domain.user.entity.UserEntity; // 사용자 엔티티 임포트
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private UserEntity member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PostCategoryEntity category;

    public static PostEntity toEntity(Post post, UserEntity userEntity, PostCategoryEntity postCategoryEntity){
        PostEntity postEntity = new PostEntity();
        postEntity.id= post.getId();
        postEntity.title = post.getTitle();
        postEntity.contents = post.getContents();
        postEntity.member = userEntity;
        postEntity.category = postCategoryEntity;

        return postEntity;
    }

    public Post toModel(){
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .category(null)
                .user(this.member.toModel())
                .build();
    }
}
