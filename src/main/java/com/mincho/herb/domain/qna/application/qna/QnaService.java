package com.mincho.herb.domain.qna.application;

import com.mincho.herb.domain.qna.dto.QnaDTO;
import com.mincho.herb.domain.qna.dto.QnaRequestDTO;
import com.mincho.herb.domain.qna.dto.QnaResponseDTO;
import com.mincho.herb.domain.qna.dto.QnaSearchConditionDTO;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QnaService {
    QnaEntity create(QnaRequestDTO dto, List<MultipartFile> files);
    QnaDTO getById(Long id);
    QnaResponseDTO getAllBySearchCondition(QnaSearchConditionDTO conditionDTO, Pageable pageable);
    void update(Long id, QnaRequestDTO dto);
    void delete(Long id);
}
