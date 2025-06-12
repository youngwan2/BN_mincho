package com.mincho.herb.domain.qna.repository.answer;

import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnswerJpaRepository extends JpaRepository<AnswerEntity, Long> {

    Optional<AnswerEntity> findByQnaId(Long qnaId);

    Boolean existsByQnaIdAndIdAndIsAdoptedTrue(Long qnaId, Long id); // 채택된 답변이 있는지 확인(중복 채택 금지)

    Boolean existsByQnaId(Long qnaId); // 이미 등록된 qna 인지 확인(질문에 답변이 있는 거라면 질문 삭제 못하도록)

    Boolean existsByQnaIdAndWriterId(Long qnaId, Long memberId); // 이미 답변을 한 qna 인지 확인

    List<AnswerEntity> findAllByWriter(UserEntity writer);
}
