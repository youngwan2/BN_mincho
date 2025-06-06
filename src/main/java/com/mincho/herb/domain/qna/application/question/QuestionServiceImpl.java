package com.mincho.herb.domain.qna.application.question;

import com.mincho.herb.domain.qna.application.questionImage.QuestionImageService;
import com.mincho.herb.domain.qna.domain.Question;
import com.mincho.herb.domain.qna.dto.*;
import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.qna.repository.questionCategory.QuestionCategoryRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
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
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(requestDTO.getCategoryId());

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

        questionImageService.imageUpload(images, questionEntity); // 이미지 업로드
        
        return questionEntity;
    }


    // 질문 수정
    @Override
    @Transactional
    public void update(Long qnaId, QuestionRequestDTO requestDTO) {
        String email = throwAuthExceptionOrReturnEmail();

        UserEntity writer = userService.getUserByEmail(email);
        QuestionEntity questionEntity = questionRepository.findById(qnaId);

        // 글쓴 사람과 수정 요청한 사람이 동일한가?
        if(!questionEntity.getWriter().getId().equals(writer.getId())) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "질문수정 권한이 없습니다.");
        }

        // 카테고리 조회
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(requestDTO.getCategoryId());

        // 태그 유효성 검증
        validateTags(requestDTO.getTags());

        // 기존 정보 수정
        questionEntity.setTitle(requestDTO.getTitle());
        questionEntity.setContent(requestDTO.getContent());
        questionEntity.setIsPrivate(requestDTO.getIsPrivate());
        questionEntity.setCategory(categoryEntity);

        // 태그 업데이트
        questionEntity.getTags().clear();
        if(requestDTO.getTags() != null && !requestDTO.getTags().isEmpty()) {
            questionEntity.getTags().addAll(requestDTO.getTags());
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

            // 특수문자 체크 (영문, 한글, 숫자, 하이픈, 언더스코어만 허용)
            if(!tag.matches("^[가-힣a-zA-Z0-9\\-_]+$")) {
                throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "태그는 영문, 한글, 숫자, 하이픈, 언더스코어만 사용 가능합니다.");
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
