package com.mincho.herb.domain.qna.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
    private Long id;
    private String content;
    private String writer;
    private Boolean isAdopted; // 채택 유무
    private Boolean isMine;
    private LocalDateTime createdAt;
    private List<String> images;
}
