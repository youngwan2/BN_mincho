package com.mincho.herb.domain.qna.application.qna;

import com.mincho.herb.domain.qna.application.qnaImage.QnaImageService;
import com.mincho.herb.domain.qna.domain.Qna;
import com.mincho.herb.domain.qna.dto.QnaDTO;
import com.mincho.herb.domain.qna.dto.QnaRequestDTO;
import com.mincho.herb.domain.qna.dto.QnaResponseDTO;
import com.mincho.herb.domain.qna.dto.QnaSearchConditionDTO;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.qna.QnaRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QnaServiceImpl implements QnaService {

    private final QnaRepository qnaRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final QnaImageService qnaImageService;
    private final CommonUtils commonUtils;

    // 질문 생성
    @Override
    public QnaEntity create(QnaRequestDTO requestDTO, List<MultipartFile> images) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);

        Qna qna = Qna.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getTitle())
                .isPrivate(requestDTO.getIsPrivate())
                .view(0L)
                .build();
        QnaEntity qnaEntity =qnaRepository.save(QnaEntity.toEntity(qna, writer)); // 질문 등록
        
        qnaImageService.imageUpload(images, qnaEntity); // 이미지 업로드
        
        return qnaEntity;
    }


    // 질문 수정
    @Override
    @Transactional
    public void update(Long qnaId, QnaRequestDTO requestDTO) {
        String email =throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);
        QnaEntity qnaEntity = qnaRepository.findById(qnaId);

        // 글쓴 사람과 수정 요청한 사람이 동일한가?
        if(!qnaEntity.getWriter().getId().equals(writer.getId())){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "질문수정 권한이 없습니다.");
        }
        qnaEntity.setTitle(requestDTO.getTitle());
        qnaEntity.setContent(requestDTO.getContent());
        qnaEntity.setIsPrivate(requestDTO.getIsPrivate());
    }


    // 질문 삭제
    @Override
    @Transactional
    public void delete(Long id) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);
        QnaEntity qnaEntity = qnaRepository.findById(id);

        // 권한 체크
        if(!qnaEntity.getWriter().getId().equals(writer.getId())){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "질문삭제 권한이 없습니다.");
        }



        // TODO: 답변이 달려 있으면 삭제 못하게 막기
        if (answerRepository.existsByQnaId(qnaEntity.getId())) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "답변이 있는 질문은 삭제할 수 없습니다.");
        }

        // 자식 테이블인 이미지 먼저 삭제
        qnaImageService.imageDelete(qnaImageService.getImages(id), qnaEntity);

        // 질문삭제
        qnaRepository.deleteById(id);
    }

    // 질문 상세 조회
    @Override
    @Transactional(readOnly = true)
    public QnaDTO getById(Long id) {
        String email = commonUtils.userCheck();
        return qnaRepository.findById(id, email);
    }


    // 질문 목록 조회
    @Override
    @Transactional(readOnly = true)
    public QnaResponseDTO getAllBySearchCondition(QnaSearchConditionDTO conditionDTO, Pageable pageable) {
        String email = commonUtils.userCheck();
        return qnaRepository.findAll(conditionDTO,pageable, email);
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
