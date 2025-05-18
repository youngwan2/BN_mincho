package com.mincho.herb.domain.qna.dto;

import java.time.LocalDateTime;

public class AnswerDTO {
    private Long id;
    private String content;
    private String writer;
    private Boolean isAdopted;
    private LocalDateTime createdAt;
}
