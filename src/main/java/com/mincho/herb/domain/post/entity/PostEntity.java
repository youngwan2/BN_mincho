package com.mincho.herb.domain.post.entity;

import com.mincho.herb.domain.post.domain.Post;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
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

    @Builder.Default
    private Boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PostCategoryEntity category;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private List<String> tags = new java.util.ArrayList<>();

    @Builder.Default
    private Boolean pined = false;


    public List<String> getTags(){
        if(this.tags.isEmpty()){
            return List.of();
        } else {
            return this.tags;
        }
    }

    public static PostEntity toEntity(Post post, UserEntity userEntity, PostCategoryEntity postCategoryEntity){
        PostEntity postEntity = new PostEntity();
        postEntity.id= post.getId();
        postEntity.title = post.getTitle();
        postEntity.contents = post.getContents();
        postEntity.isDeleted = post.getIsDeleted();
        postEntity.user = userEntity;
        postEntity.category = postCategoryEntity;
        postEntity.tags = post.getTags();
        postEntity.pined = post.getPined();

        return postEntity;
    }

    public Post toModel(){
        return Post.builder()
                .id(this.id)
                .title(this.title)
                .contents(this.contents)
                .category(this.category.toModel())
                .isDeleted(this.isDeleted)
                .user(this.user.toModel())
                .pined(this.pined)
                .tags(this.tags)
                .build();
    }

    public void changeIsDeleted(boolean b) {
        this.isDeleted = b;
    }
}
