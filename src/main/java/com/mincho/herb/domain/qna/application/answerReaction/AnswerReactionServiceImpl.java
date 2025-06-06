package com.mincho.herb.domain.qna.application.answerReaction;

import com.mincho.herb.domain.qna.dto.AnswerReactionRequestDTO;
import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity;
import com.mincho.herb.domain.qna.entity.AnswerReactionEntity.ReactionType;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.answerReaction.AnswerReactionRepository;
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
public class AnswerReactionServiceImpl implements AnswerReactionService {

    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final AnswerReactionRepository answerReactionRepository;
    private final AuthUtils authUtils;

    @Override
    @Transactional
    public void reactToAnswer(Long answerId, AnswerReactionRequestDTO requestDTO) {
        // 현재 사용자 확인
        String email = authUtils.userCheck();
        UserEntity user = getUserEntityByEmail(email);


        // 답변 조회
        AnswerEntity answer = answerRepository.findById(answerId);

        // 자신의 답변에는 반응할 수 없음
        if (answer.getWriter().getId().equals(user.getId())) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "자신의 답변에는 반응할 수 없습니다.");
        }

        // 반응 타입 변환
        ReactionType reactionType;
        try {
            reactionType = ReactionType.valueOf(requestDTO.getReactionType());
        } catch (IllegalArgumentException e) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효하지 않은 반응 타입입니다. (LIKE 또는 DISLIKE)");
        }

        // 기존 반응 조회
        AnswerReactionEntity existingReaction = answerReactionRepository.findByUserAndAnswer(user, answer);

        if (existingReaction != null) {
            // 이미 같은 반응이 있으면 취소 (예: LIKE → LIKE)
            if (existingReaction.getReactionType() == reactionType) {
                answerReactionRepository.delete(existingReaction);
            } else {
                // 다른 반응으로 변경 (예: LIKE → DISLIKE)
                existingReaction.setReactionType(reactionType);
                answerReactionRepository.save(existingReaction);
            }
        } else {
            // 새 반응 저장
            AnswerReactionEntity reaction = AnswerReactionEntity.builder()
                    .user(user)
                    .answer(answer)
                    .reactionType(reactionType)
                    .build();
            answerReactionRepository.save(reaction);
        }
    }

    @Override
    @Transactional
    public void cancelReaction(Long answerId) {
        // 현재 사용자 확인
        String email = authUtils.userCheck();
        UserEntity user = getUserEntityByEmail(email);

        // 답변 조회
        AnswerEntity answer = answerRepository.findById(answerId);

        // 반응 조회 후 삭제
        AnswerReactionEntity reaction = answerReactionRepository.findByUserAndAnswer(user, answer);
        if (reaction != null) {
            answerReactionRepository.delete(reaction);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long countReactions(Long answerId, String reactionTypeStr) {
        ReactionType reactionType;
        try {
            reactionType = ReactionType.valueOf(reactionTypeStr);
        } catch (IllegalArgumentException e) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "유효하지 않은 반응 타입입니다. (LIKE 또는 DISLIKE)");
        }

        return answerReactionRepository.countByAnswerIdAndReactionType(answerId, reactionType);
    }


    private UserEntity getUserEntityByEmail(String email) {
        return userService.getUserByEmail(email);
    }
}
