package com.mincho.herb.domain.qna.application;

import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;

public interface AnswerService {
    void create(Long qnaId, AnswerRequestDTO requestDTO);
}
