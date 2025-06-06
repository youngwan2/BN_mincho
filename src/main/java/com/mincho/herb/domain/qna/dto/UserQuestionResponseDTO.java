package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserQuestionResponseDTO {
    private List<UserQuestionSummaryDTO> qnas;
    private Long totalCount;
}
