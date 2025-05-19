package com.mincho.herb.domain.qna.application;

import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.qna.QnaRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {
    private final QnaRepository qnaRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final CommonUtils commonUtils;

    @Override
    public void create(Long qnaId, AnswerRequestDTO requestDTO) {
        String email =throwAuthExceptionOrReturnEmail();
        QnaEntity qna = qnaRepository.findById(qnaId);
        MemberEntity writer = userService.getUserByEmail(email);

        AnswerEntity entity = AnswerEntity.builder()
                .qna(qna)
                .writer(writer)
                .content(requestDTO.getContent())
                .isAdopted(false)
                .build();

        answerRepository.save(entity);
    }

    // 유저 체크(성공 시 유저 이메일 반환)
    private String throwAuthExceptionOrReturnEmail(){
        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용 가능합니다.");
        }
        return email;
    }
}
