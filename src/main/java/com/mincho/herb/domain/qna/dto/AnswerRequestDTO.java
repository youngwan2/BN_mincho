package com.mincho.herb.domain.qna.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerRequestDTO {
    @Size(min = 20, max = 1000, message = "최소 20자 이상 최대 1000자 까지 허용됩니다.")
    private String content;
    private Boolean isAdopted;
}
