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
public class PostDTO {
    private Long id;
    private String title;
    private PostCategoryDTO category;
    private String nickname;
    private Long likeCount;
    private Long viewCount;
    private LocalDateTime createdAt;
    private Boolean newPost; // 새 게시글 여부
    private Boolean isMine; // 현재 사용자의 글인지 여부
}
