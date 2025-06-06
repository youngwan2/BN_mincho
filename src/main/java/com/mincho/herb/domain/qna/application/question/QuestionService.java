package com.mincho.herb.domain.qna.application.question;

import com.mincho.herb.domain.qna.dto.*;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionService {
    QuestionEntity create(QuestionRequestDTO dto, List<MultipartFile> files);
    QuestionDTO getById(Long id);
    QuestionResponseDTO getAllBySearchCondition(QuestionSearchConditionDTO conditionDTO, Pageable pageable);
    List<QuestionCategoryDTO> getAllCategories();
    void update(Long id, QuestionRequestDTO dto);
    void delete(Long id);
    UserQuestionResponseDTO getAllByUserId(Long userId, Pageable pageable);
}
