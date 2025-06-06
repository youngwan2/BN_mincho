package com.mincho.herb.domain.qna.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCategory {
    private Long id;
    private String name;
    private String description;
}
