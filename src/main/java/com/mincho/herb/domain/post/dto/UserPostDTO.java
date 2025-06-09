package com.mincho.herb.domain.post.dto;

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
public class UserPostDTO {
    private Long id; // 게시글 ID
    private String title; // 게시글 제목
    private String contents; // 게시글 내용
    private Boolean isPrivate; // 비공개 여부
    private Boolean isMine; // 작성자 여부
    private String writer; // 작성자 이름
    private Long likeCount; // 좋아요 수
    private Long viewCount; // 조회수
    private List<String> tags;
    private PostCategoryDTO category;
    private LocalDateTime createdAt; // 작성일시

}
