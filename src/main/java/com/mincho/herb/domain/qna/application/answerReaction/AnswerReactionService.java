package com.mincho.herb.domain.qna.application.answerReaction;

import com.mincho.herb.domain.qna.dto.AnswerReactionRequestDTO;

public interface AnswerReactionService {

    /**
     * 답변에 대한 반응(좋아요/싫어요)을 처리합니다.
     *
     * @param answerId 답변 ID
     * @param requestDTO 반응 요청 DTO
     */
    void reactToAnswer(Long answerId, AnswerReactionRequestDTO requestDTO);

    /**
     * 답변에 대한 반응을 취소합니다.
     *
     * @param answerId 답변 ID
     */
    void cancelReaction(Long answerId);

    /**
     * 답변에 대한 특정 타입의 반응 개수를 조회합니다.
     *
     * @param answerId 답변 ID
     * @param reactionType 반응 타입 ("LIKE" 또는 "DISLIKE")
     * @return 해당 타입의 반응 개수
     */
    Long countReactions(Long answerId, String reactionType);
}
