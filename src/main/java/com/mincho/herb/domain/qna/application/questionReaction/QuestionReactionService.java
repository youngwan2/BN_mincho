package com.mincho.herb.domain.qna.application.questionReaction;

import com.mincho.herb.domain.qna.dto.QuestionReactionRequestDTO;

public interface QuestionReactionService {

    /**
     * 질문에 대한 반응(좋아요/싫어요)을 처리합니다.
     *
     * @param questionId 질문 ID
     * @param requestDTO 반응 요청 DTO
     */
    void reactToQuestion(Long questionId, QuestionReactionRequestDTO requestDTO);

    /**
     * 질문에 대한 반응을 취소합니다.
     *
     * @param questionId 질문 ID
     */
    void cancelReaction(Long questionId);

    /**
     * 질문에 대한 특정 타입의 반응 개수를 조회합니다.
     *
     * @param questionId 질문 ID
     * @param reactionType 반응 타입 ("LIKE" 또는 "DISLIKE")
     * @return 해당 타입의 반응 개수
     */
    Long countReactions(Long questionId, String reactionType);
}
