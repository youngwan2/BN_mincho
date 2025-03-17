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
    private String category;

    public static PostCategoryEntity toEntity(PostCategory postCategory){
        PostCategoryEntity postCategoryEntity = new PostCategoryEntity();

        postCategoryEntity.id = postCategory.getId();
        postCategoryEntity.category = postCategory.getCategory();

        return postCategoryEntity;

    }

    public PostCategory toModel(){
        return PostCategory.builder()
                .id(this.id)
                .category(this.category)
                .build();
    }
}
