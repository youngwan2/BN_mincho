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
public class QnaDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private Boolean isMine;
    private String writer;
    private List<String> imageUrls;
    private List<AnswerDTO> answers;
    private LocalDateTime createdAt;
    private Long view;
}
