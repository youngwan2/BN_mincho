package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionSummaryDTO {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
