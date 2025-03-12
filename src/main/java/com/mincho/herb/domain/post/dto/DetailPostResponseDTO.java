package com.mincho.herb.domain.post.dto;


import com.mincho.herb.domain.post.domain.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DetailPostResponseDTO {
    private Long id;
    private String title;
    private String contents;
    private String category;
    private Author author;
    private Long likeCount;
    private LocalDateTime createdAt;

}
