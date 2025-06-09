package com.mincho.herb.domain.qna.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerReactionRequestDTO {
    @NotNull(message = "반응 타입은 필수입니다.")
    private String reactionType; // "LIKE" 또는 "DISLIKE"
}
