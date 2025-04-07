package com.mincho.herb.domain.post.dto;


import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Builder
@Data
public class MypagePostsDTO {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
}
