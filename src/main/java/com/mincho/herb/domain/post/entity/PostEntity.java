package com.mincho.herb.domain.post.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.user.entity.MemberEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String contents;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PostCategoryEntity category;

    public static PostEntity toEntity(Post post, MemberEntity memberEntity, PostCategoryEntity postCategoryEntity){
        PostEntity postEntity = new PostEntity();
        postEntity.id= post.getId();
        postEntity.title = post.getTitle();
        postEntity.contents = post.getContents();
        postEntity.member = memberEntity;
        postEntity.category = postCategoryEntity;

        return postEntity;
    }

    public Post toModel(){
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .category(null)
                .member(this.member.toModel())
                .build();
    }
}
