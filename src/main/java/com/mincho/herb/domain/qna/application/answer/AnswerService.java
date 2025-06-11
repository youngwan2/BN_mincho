package com.mincho.herb.domain.qna.application.answer;

import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnswerService {
    void create(Long qnaId, AnswerRequestDTO requestDTO, List<MultipartFile> images);
    void update(Long id, AnswerRequestDTO dto);
    void delete(Long id);
    void adopt(Long qnaId, Long answerId);

}
