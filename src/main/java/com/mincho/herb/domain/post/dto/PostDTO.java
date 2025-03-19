package com.mincho.herb.domain.post.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDTO {
    private Long id;
    private String title;
    private String category;
    private String nickname;
    private Long likeCount;
    private LocalDateTime createdAt;
}
