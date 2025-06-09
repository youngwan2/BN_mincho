package com.mincho.herb.domain.qna.application.answer;

import com.mincho.herb.domain.qna.application.answerImage.AnswerImageService;
import com.mincho.herb.domain.qna.dto.AnswerRequestDTO;
import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
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
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final AnswerImageService answerImageService;
    private final UserService userService;
    private final AuthUtils authUtils;

    // 답변 등록
    @Override
    @Transactional
    public void create(Long qnaId, AnswerRequestDTO requestDTO, List<MultipartFile> images) {
        String email =throwAuthExceptionOrReturnEmail();
        QuestionEntity qna = questionRepository.findById(qnaId);
        UserEntity writer = userService.getUserByEmail(email);

        // 질문자 본인의 질문이라면 답변 금지
        if(qna.getWriter().equals(writer)){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "본인의 질문에 답변할 수 없습니다.");
        }

        // 이미 답변자가 답변을 적은 글이라면 중복 답변 금지
        if(answerRepository.existsByQnaIdAndWriterId(qnaId, writer.getId())){
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 답변을 작성한 질문입니다.");
        }

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

        UserEntity member = userService.getUserByEmail(email); // 수정 요청한 유저
        AnswerEntity answerEntity= answerRepository.findById(id);
        UserEntity writer = answerEntity.getWriter(); // 답변 작성자

        // 요청 유저와 답변 작성자가 동일한가?
        if(!writer.equals(member)) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "답변수정 권한이 없습니다.");
        }
        answerEntity.setContent(dto.getContent());
    }

    // 답변 삭제
    @Override
    @Transactional
    public void delete(Long answerId) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity member =userService.getUserByEmail(email);
        AnswerEntity answerEntity= answerRepository.findById(answerId);
        UserEntity writer = answerEntity.getWriter(); // 답변 작성자

        // 요청 유저와 답변 작성자가 동일인인가?
        if(!writer.equals(member)) {
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "답변삭제 권한이 없습니다.");
        }

        // 답변 삭제 전 이미지 삭제
        answerImageService.imageDelete(answerImageService.getImages(answerId), answerEntity);

        // 답변 삭제
        answerRepository.deleteById(answerId);
    }


    // 답변 채택
    @Override
    public void adopt(Long answerId) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);
        AnswerEntity answerEntity= answerRepository.findById(answerId);
        QuestionEntity questionEntity = questionRepository.findById(answerId);

        // 요청 유저와 질문자가 동일한가?
        if(!writer.equals(questionEntity.getWriter())){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS, "답변채택 권한이 없습니다.");
        }

        // 이미 채택된 답변이 있는가?
        if(answerRepository.existsByQnaIdAndIdAndIsAdoptedTrue(questionEntity.getId(), answerId)){
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 채택된 답변이 있습니다.");
        }

        // 답변 채택
        answerEntity.setIsAdopted(true);
    }

    // 유저 체크(성공 시 유저 이메일 반환)
    private String throwAuthExceptionOrReturnEmail(){
        String email = authUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용 가능합니다.");
        }
        return email;
    }
}
