package com.mincho.herb.domain.post.entity;

import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "Post")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PostEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;


    @Column(columnDefinition = "TEXT")
    private String contents;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PostCategoryEntity category;

    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags;

    public static PostEntity toEntity(Post post, UserEntity userEntity, PostCategoryEntity postCategoryEntity){
        PostEntity postEntity = new PostEntity();
        postEntity.id= post.getId();
        postEntity.title = post.getTitle();
        postEntity.contents = post.getContents();
        postEntity.user = userEntity;
        postEntity.category = postCategoryEntity;

        return postEntity;
    }

    public Post toModel(){
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .category(this.category.getCategory())
                .user(this.user.toModel())
                .build();
    }
}
