package com.mincho.herb.domain.post.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostCategory {
    private Long id;
    private String category;

    public PostCategory withCategory(String category){
        return PostCategory.builder()
                .id(this.id)
                .category(category)
                .build();
    }
}
