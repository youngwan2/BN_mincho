package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.domain.Author;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ResponsePostDTO {
    private Long id;
    private String title;
    private String category;
    private Author author;
    private Long likeCount;
    private LocalDateTime createdAt;
}
