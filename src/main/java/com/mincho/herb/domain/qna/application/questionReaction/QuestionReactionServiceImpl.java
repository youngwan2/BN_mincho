package com.mincho.herb.domain.qna.application.questionReaction;

import com.mincho.herb.domain.qna.dto.QuestionReactionRequestDTO;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity;
import com.mincho.herb.domain.qna.entity.QuestionReactionEntity.ReactionType;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.qna.repository.questionReaction.QuestionReactionRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionReactionServiceImpl implements QuestionReactionService {

    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final QuestionReactionRepository questionReactionRepository;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public void reactToQuestion(Long questionId, QuestionReactionRequestDTO requestDTO) {
        // 현재 사용자 확인
        String email = authUtils.userCheck();
        UserEntity user = getUserEntityByEmail(email);

        // 질문 조회
        QuestionEntity question = questionRepository.findById(questionId);

        // 자신의 질문에는 반응할 수 없음
        if (question.getWriter().getId().equals(user.getId())) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "자신의 질문에는 반응할 수 없습니다.");
        }

        // 반응 타입 변환
        ReactionType reactionType;
        try {
            reactionType = ReactionType.valueOf(requestDTO.getReactionType());
        } catch (IllegalArgumentException e) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효하지 않은 반응 타입입니다. (LIKE 또는 DISLIKE)");
        }

        // 기존 반응 조회
        QuestionReactionEntity existingReaction = questionReactionRepository.findByUserAndQuestion(user, question);

        if (existingReaction != null) {
            // 이미 같은 반응이 있으면 취소 (예: LIKE → LIKE)
            if (existingReaction.getReactionType() == reactionType) {
                questionReactionRepository.delete(existingReaction);
            } else {
                // 다른 반응으로 변경 (예: LIKE → DISLIKE)
                existingReaction.setReactionType(reactionType);
                questionReactionRepository.save(existingReaction);
            }
        } else {
            // 새 반응 저장
            QuestionReactionEntity reaction = QuestionReactionEntity.builder()
                    .user(user)
                    .question(question)
                    .reactionType(reactionType)
                    .build();
            questionReactionRepository.save(reaction);
        }
    }

    @Override
    @Transactional
    public void cancelReaction(Long questionId) {
        // 현재 사용자 확인
        String email = authUtils.userCheck();
        UserEntity user = getUserEntityByEmail(email);

        // 질문 조회
        QuestionEntity question = questionRepository.findById(questionId);

        // 반응 조회 후 삭제
        QuestionReactionEntity reaction = questionReactionRepository.findByUserAndQuestion(user, question);
        if (reaction != null) {
            questionReactionRepository.delete(reaction);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReactions(Long questionId, String reactionTypeStr) {
        ReactionType reactionType;
        try {
            reactionType = ReactionType.valueOf(reactionTypeStr);
        } catch (IllegalArgumentException e) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효하지 않은 반응 타입입니다. (LIKE 또는 DISLIKE)");
        }

        return questionReactionRepository.countByQuestionIdAndReactionType(questionId, reactionType);
    }

    private UserEntity getUserEntityByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}
