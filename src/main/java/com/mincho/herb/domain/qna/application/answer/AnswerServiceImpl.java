package com.mincho.herb.domain.qna.application.answer;

import com.mincho.herb.domain.qna.application.answerImage.AnswerImageService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerServiceImpl implements AnswerService {
    private final QnaRepository qnaRepository;
    private final AnswerRepository answerRepository;
    private final AnswerImageService answerImageService;
    private final UserService userService;
    private final CommonUtils commonUtils;

    // 답변 등록
    @Override
    @Transactional
    public void create(Long qnaId, AnswerRequestDTO requestDTO, List<MultipartFile> images) {
        String email =throwAuthExceptionOrReturnEmail();
        QnaEntity qna = qnaRepository.findById(qnaId);
        MemberEntity writer = userService.getUserByEmail(email);

        AnswerEntity entity = AnswerEntity.builder()
                .qna(qna)
                .writer(writer)
                .content(requestDTO.getContent())
                .isAdopted(false)
                .build();

        AnswerEntity answerEntity= answerRepository.save(entity);

        answerImageService.imageUpload(images, answerEntity);
    }

    
    // 답변 수정
    @Override
    @Transactional
    public void update(Long id, AnswerRequestDTO dto) {
        String email = throwAuthExceptionOrReturnEmail();

        MemberEntity writer = userService.getUserByEmail(email);


        AnswerEntity answerEntity= answerRepository.findById(id);

        MemberEntity memberEntity = answerEntity.getWriter();
        if(!writer.equals(memberEntity)) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "답변수정 권한이 없습니다.");
        }
        answerEntity.setContent(dto.getContent());
    }

    // 답변 삭제
    @Override
    @Transactional
    public void delete(Long id) {



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
