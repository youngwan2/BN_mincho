package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private Boolean isMine;
    private Long writerId;
    private String writer;
    private String avatarUrl;
    private List<String> imageUrls;
    private List<AnswerDTO> answers;
    private List<String> tags;
    private LocalDateTime createdAt;
    private Long view;
    private Long categoryId;     // 카테고리 ID
    private String categoryName; // 카테고리 이름
}
