package com.mincho.herb.domain.qna.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private Long writerId;
    private Long categoryId;
    private Long view;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
