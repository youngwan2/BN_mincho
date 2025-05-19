package com.mincho.herb.domain.qna.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Qna {
    private Long id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private Long writerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long view;
}
