package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.domain.Author;
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
    private Author author; // 사용자 정보 (닉네임, 프로필 이미지)
    private Long likeCount;
    private Object viewCount;
    private LocalDateTime createdAt;
    private Boolean newPost; // 새 게시글 여부
    private Boolean isMine; // 현재 사용자의 글인지 여부
}
