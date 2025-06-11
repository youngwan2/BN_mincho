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

    /**
     * 특정 사용자가 작성한 질문 목록을 조회합니다.
     * 비공개 질문은 요청자가 작성자 본인인 경우에만 조회되고,
     * 그렇지 않은 경우 공개 질문만 조회됩니다.
     *
     * @param userId 조회할 사용자 ID
     * @param pageable 페이징 정보
     * @return 사용자가 작성한, 접근 권한이 있는 질문 목록
     */
    UserQuestionResponseDTO getAllByUserId(Long userId, Pageable pageable);

    /**
     * 질문의 조회수를 증가시킵니다.
     *
     * @param qnaId 조회수를 증가시킬 질문 ID
     */
    void increaseViewCount(Long qnaId);
}
