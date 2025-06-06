package com.mincho.herb.domain.qna.repository.question;

import com.mincho.herb.domain.qna.dto.QuestionDTO;
import com.mincho.herb.domain.qna.dto.QuestionResponseDTO;
import com.mincho.herb.domain.qna.dto.QuestionSearchConditionDTO;
import com.mincho.herb.domain.qna.dto.UserQuestionResponseDTO;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository {

    QuestionEntity save(QuestionEntity questionEntity);

    QuestionEntity findById(Long id);

    QuestionDTO findById(Long id, String email);

    QuestionResponseDTO findAll(QuestionSearchConditionDTO conditionDTO, Pageable pageable, String email);

    UserQuestionResponseDTO findAllByUserId(Long userId, Pageable pageable);

    void deleteById(Long qnaId);
}
