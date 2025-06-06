package com.mincho.herb.domain.post.entity;

import com.mincho.herb.domain.post.domain.PostCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PostCategory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PostCategoryTypeEnum type;

    private String name;
    private String description;

    public static PostCategoryEntity toEntity(PostCategory postCategory){
        PostCategoryEntity postCategoryEntity = new PostCategoryEntity();
        postCategoryEntity.id = postCategory.getId();
        postCategoryEntity.name = postCategory.getName();
        postCategoryEntity.type = postCategory.getType();
        postCategoryEntity.description = postCategory.getDescription();
        return postCategoryEntity;
    }

    public PostCategory toModel(){
        return PostCategory.builder()
                .id(this.id)
                .name(this.name)
                .type(this.type)
                .description(this.getDescription())
                .build();
    }
}
