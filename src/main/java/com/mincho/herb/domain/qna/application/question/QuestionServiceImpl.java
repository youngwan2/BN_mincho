package com.mincho.herb.domain.qna.application.question;

import com.mincho.herb.domain.qna.application.questionImage.QuestionImageService;
import com.mincho.herb.domain.qna.domain.Question;
import com.mincho.herb.domain.qna.dto.*;
import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.qna.repository.questionCategory.QuestionCategoryRepository;
import com.mincho.herb.domain.qna.repository.questionReaction.QuestionReactionRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionReactionRepository questionReactionRepository;
    private final AnswerRepository answerRepository;
    private final UserService userService;
    private final QuestionImageService questionImageService;
    private final QuestionCategoryRepository questionCategoryRepository;
    private final AuthUtils authUtils;

    // 질문 생성
    @Override
    @Transactional
    public QuestionEntity create(QuestionRequestDTO requestDTO, List<MultipartFile> images) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);

        // 카테고리 조회
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(requestDTO.getCategory());

        // 태그 유효성 검증 (선택적으로 추가)
        validateTags(requestDTO.getTags());

        Question question = Question.builder()
                .title(requestDTO.getTitle())
                .content(requestDTO.getContent())
                .isPrivate(requestDTO.getIsPrivate())
                .categoryId(categoryEntity.getId())
                .tags(requestDTO.getTags())
                .view(0L)
                .build();

        QuestionEntity questionEntity = questionRepository.save(QuestionEntity.toEntity(question, writer, categoryEntity)); // 질문 등록

        if(images !=null){
            questionImageService.imageUpload(images, questionEntity); // 이미지 업로드
        }
        
        return questionEntity;
    }


    // 질문 수정
    @Override
    @Transactional
    public void update(Long qnaId, QuestionRequestDTO requestDTO) {
        log.debug("1. qnaId 로 질문 수정 시작: {}, requestDTO: {}", qnaId, requestDTO);

        String email = throwAuthExceptionOrReturnEmail();
        log.debug("2. 인증된 유저 이메일 확인: {}", email);

        UserEntity writer = userService.getUserByEmail(email);
        log.debug("3. 질문 작성자 정보 조회: {}", writer);

        QuestionEntity questionEntity = questionRepository.findById(qnaId);
        log.debug("4. 질문 정보 조회: {}", questionEntity);

        // 글쓴 사람과 수정 요청한 사람이 동일한가?
        if (!questionEntity.getWriter().getId().equals(writer.getId())) {
            log.warn("인증 안 된 유저의 ID: {}", writer.getId());
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "질문수정 권한이 없습니다.");
        }

        // 카테고리 조회
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(requestDTO.getCategory());
        log.debug("5. 카테고리 정보 조회: {}", categoryEntity);

        // 태그 유효성 검증
        validateTags(requestDTO.getTags());
        log.debug("6. 태그 유효성 검증 성공 시 보임: {}", requestDTO.getTags());

        // 기존 정보 수정
        questionEntity.setTitle(requestDTO.getTitle());
        questionEntity.setContent(requestDTO.getContent());
        questionEntity.setIsPrivate(requestDTO.getIsPrivate());
        questionEntity.setCategory(categoryEntity);
        log.debug("7. 새로운 값으로 질문이 업데이트 되면 보임: {}", questionEntity);

        // 태그 업데이트
        questionEntity.getTags().clear();
        log.debug("8. 존재하는 태그를 모두 초기화하면 보임.");

        if (requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            log.debug("9. 새로운 태그 추가 시작: {}", requestDTO.getTags());
            questionEntity.setTags(requestDTO.getTags());
            log.debug("9-2.새로운 태그 추가 완료(모든 변경 사항 반영 성공): {}", requestDTO.getTags());
        }
    }


    // 질문 삭제
    @Override
    @Transactional
    public void delete(Long id) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);
        QuestionEntity questionEntity = questionRepository.findById(id);

        // 권한 체크
        if(!questionEntity.getWriter().getId().equals(writer.getId())) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "질문삭제 권한이 없습니다.");
        }

        // 답변이 달려 있으면 삭제 못하게 막기
        if (answerRepository.existsByQnaId(questionEntity.getId())) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "답변이 있는 질문은 삭제할 수 없습니다.");
        }

        // 자식 테이블인 이미지 먼저 삭제
        questionImageService.imageDelete(questionImageService.getImages(id), questionEntity);

        // 자식 테이블인 좋아요/싫어요 테이블 삭제
        questionReactionRepository.deleteAllByQuestion(questionEntity);

        // 질문삭제
        questionRepository.deleteById(id);
    }

    // 질문 상세 조회
    @Override
    @Transactional(readOnly = true)
    public QuestionDTO getById(Long id) {
        String email = authUtils.userCheck();
        return questionRepository.findById(id, email);
    }


    // 질문 목록 조회
    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getAllBySearchCondition(QuestionSearchConditionDTO conditionDTO, Pageable pageable) {
        String email = authUtils.userCheck();
        return questionRepository.findAll(conditionDTO, pageable, email);
    }

    // 사용자별 질문 목록 조회
    @Override
    @Transactional(readOnly = true)
    public UserQuestionResponseDTO getAllByUserId(Long userId, Pageable pageable) {
        // 해당 사용자의 질문 목록 조회 (비공개 질문은 제외)
        return questionRepository.findAllByUserId(userId, pageable);
    }

    // 카테고리 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<QuestionCategoryDTO> getAllCategories() {
        return questionCategoryRepository.findAll().stream()
                .map(category -> new QuestionCategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .toList();
    }

    /**
     * 질문의 조회수를 1 증가시킵니다.
     *
     * @param qnaId 조회수를 증가시킬 질문 ID
     */
    @Override
    @Transactional
    public void increaseViewCount(Long qnaId) {
        // 질문 ID로 질문 엔티티를 조회합니다.
        QuestionEntity questionEntity = questionRepository.findById(qnaId);

        // 현재 조회수를 가져와서 1 증가시킵니다.
        Long currentViewCount = questionEntity.getView();
        questionEntity.setView(currentViewCount + 1);

        // 변경된 엔티티를 저장합니다.
        questionRepository.save(questionEntity);

        log.info("질문 ID: {}의 조회수가 {} -> {}로 증가했습니다.",
                qnaId, currentViewCount, questionEntity.getView());
    }

    /**
     * 태그 유효성 검증
     *
     * @param tags 태그 목록
     */
    private void validateTags(List<String> tags) {
        if(tags == null) {
            return;
        }

        // 태그 개수 제한
        if(tags.size() > 5) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "태그는 최대 5개까지 입력 가능합니다.");
        }

        // 태그 길이 및 특수문자 제한
        for(String tag : tags) {
            if(tag == null || tag.trim().isEmpty()) {
                throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "빈 태그는 입력할 수 없습니다.");
            }

            if(tag.length() > 20) {
                throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "태그는 20자 이내로 입력해야 합니다.");
            }

        }
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
