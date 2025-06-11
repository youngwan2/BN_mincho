package com.mincho.herb.domain.post.dto;

import com.mincho.herb.domain.post.domain.Author;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailPostDTO {
    private Long id;
    private String title;
    private String contents;
    private Author author;
    private PostCategoryDTO category;
    private Boolean isMine;
    private Long likeCount;
    private Long viewCount;
    private LocalDateTime createdAt;
    private List<String> tags; // 게시글의 태그 목록
    private Boolean isDeleted; // 삭제 여부 필드 추가
}
