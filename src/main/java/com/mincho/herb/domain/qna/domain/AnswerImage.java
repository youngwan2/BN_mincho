package com.mincho.herb.domain.qna.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerImage {
    private Long id;
    private String imageUrl;
}
