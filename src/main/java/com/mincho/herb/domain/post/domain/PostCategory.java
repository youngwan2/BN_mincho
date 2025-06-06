package com.mincho.herb.domain.post.domain;

import com.mincho.herb.domain.post.entity.PostCategoryTypeEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostCategory {
    private Long id;
    private PostCategoryTypeEnum type;
    private String name;
    private String description;
}
