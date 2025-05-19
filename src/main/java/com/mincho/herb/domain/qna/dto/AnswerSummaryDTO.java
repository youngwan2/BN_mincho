package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerSummaryDTO {
        private Long id;
        private String writer;
        private Boolean isAdopted; // 채택 유무
        private Boolean isMine;
        private LocalDateTime createdAt;
}
