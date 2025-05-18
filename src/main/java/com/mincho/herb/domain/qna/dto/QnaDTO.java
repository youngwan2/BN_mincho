package com.mincho.herb.domain.qna.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QnaDTO {
    private Long id;
    private String title;
    private String content;
    private Boolean isPrivate;
    private String writerNickname;
    private List<AnswerDTO> answers;
    private LocalDateTime createdAt;
}
