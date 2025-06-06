package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.entity.PostCategoryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostCategoryInfoDTO {
    private Long id;
    private String name;
    private PostCategoryTypeEnum type; // 카테고리 타입 (예: DAILY, EXPERIENCE 등)
    private String description; // 카테고리 설명
    private Long count;
}
