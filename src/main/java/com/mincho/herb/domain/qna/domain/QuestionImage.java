package com.mincho.herb.domain.qna.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionImage {
    private Long id;
    private String imageUrl;
}
