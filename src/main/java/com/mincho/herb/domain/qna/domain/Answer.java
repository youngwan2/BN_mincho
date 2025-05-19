package com.mincho.herb.domain.qna.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Answer {
    private Long id;
    private String content;
    private Long qnaId;
    private Long writerId;
    private Boolean isAdopted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
