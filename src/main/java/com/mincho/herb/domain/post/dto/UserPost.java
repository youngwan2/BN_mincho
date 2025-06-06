package com.mincho.herb.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPost {
    private Long id; // 게시글 ID
    private String title; // 게시글 제목
    private PostCategoryDTO category;
    private LocalDateTime createdAt; // 작성일시

}
