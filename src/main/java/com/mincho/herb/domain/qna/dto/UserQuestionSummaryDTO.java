package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

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
