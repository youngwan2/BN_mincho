package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.entity.PostCategoryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostCategoryDTO {
    private Long id;
    private PostCategoryTypeEnum type;
    private String name;
    private String description;
}
