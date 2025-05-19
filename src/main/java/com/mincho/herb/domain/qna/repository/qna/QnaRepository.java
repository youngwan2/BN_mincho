package com.mincho.herb.domain.qna.repository.qna;

import com.mincho.herb.domain.qna.dto.QnaDTO;
import com.mincho.herb.domain.qna.dto.QnaResponseDTO;
import com.mincho.herb.domain.qna.dto.QnaSearchConditionDTO;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import org.springframework.data.domain.Pageable;

public interface QnaRepository{

    QnaEntity save(QnaEntity qnaEntity);

    QnaEntity findById(Long id);

    QnaDTO findById(Long id, String email);

    QnaResponseDTO findAll(QnaSearchConditionDTO conditionDTO, Pageable pageable, String email);

    void deleteById(Long qnaId);
}
