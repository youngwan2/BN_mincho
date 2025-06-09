package com.mincho.herb.domain.qna.application.questionCategory;

import com.mincho.herb.domain.qna.dto.QuestionCategoryCreateDTO;
import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;
import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import com.mincho.herb.domain.qna.repository.questionCategory.QuestionCategoryRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    private final QuestionCategoryRepository questionCategoryRepository;

    // 기본 카테고리 목록 정의
    private static final List<QuestionCategoryCreateDTO> DEFAULT_CATEGORIES = Arrays.asList(
        new QuestionCategoryCreateDTO("약초 활용법", "약초의 다양한 활용 방법에 대한 질문"),
        new QuestionCategoryCreateDTO("약초 식별", "약초 종류 식별 및 특성에 관한 질문"),
        new QuestionCategoryCreateDTO("약초 재배", "약초 재배 방법 및 환경에 관한 질문"),
        new QuestionCategoryCreateDTO("약초 효능", "약초의 효능 및 약리적 특성에 관한 질문"),
        new QuestionCategoryCreateDTO("약초 레시피", "약초를 활용한 요리, 차, 팅크 등의 레시피 관련 질문"),
        new QuestionCategoryCreateDTO("약초 약용", "약초의 약용 활용에 관한 질문"),
        new QuestionCategoryCreateDTO("기타", "기타 약초 관련 질문")
    );

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

    @Override
    @Transactional
    public QuestionCategoryDTO createCategory(QuestionCategoryCreateDTO categoryDTO) {
        // 중복 이름 체크
        if (questionCategoryRepository.existsByName(categoryDTO.getName())) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 존재하는 카테고리 이름입니다.");
        }

        QuestionCategoryEntity categoryEntity = QuestionCategoryEntity.builder()
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .build();

        QuestionCategoryEntity savedCategory = questionCategoryRepository.save(categoryEntity);

        log.info("새로운 질문 카테고리가 생성되었습니다: {}", savedCategory);

        return new QuestionCategoryDTO(
                savedCategory.getId(),
                savedCategory.getName(),
                savedCategory.getDescription()
        );
    }

    @Override
    @Transactional
    public QuestionCategoryDTO updateCategory(Long categoryId, QuestionCategoryCreateDTO categoryDTO) {
        // 카테고리 존재 여부 확인
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(categoryId);

        // 이름�� 변경된 경우 중복 체크
        if (!categoryEntity.getName().equals(categoryDTO.getName()) &&
                questionCategoryRepository.existsByName(categoryDTO.getName())) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이미 존재하는 카테고리 이름입니다.");
        }

        // 카테고리 정보 업데이트
        categoryEntity.setName(categoryDTO.getName());
        categoryEntity.setDescription(categoryDTO.getDescription());

        QuestionCategoryEntity updatedCategory = questionCategoryRepository.save(categoryEntity);

        log.info("질문 카테고리가 수정되었습니다: {}", updatedCategory);

        return new QuestionCategoryDTO(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getDescription()
        );
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        // 카테고리 존재 여부 확인
        QuestionCategoryEntity categoryEntity = questionCategoryRepository.findById(categoryId);

        // 카테고리에 연결된 질문이 있는지 확인
        if (questionCategoryRepository.hasRelatedQuestions(categoryId)) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT, "이 카테고리에 연결된 질문이 있어 삭제할 수 없습니다.");
        }

        questionCategoryRepository.delete(categoryEntity);
        log.info("질문 카테고리 ID: {}가 삭제되었습니다.", categoryId);
    }

    @Override
    @Transactional
    public List<QuestionCategoryDTO> initializeCategories() {
        List<QuestionCategoryEntity> existingCategories = questionCategoryRepository.findAll();

        // 기존 카테고리들 중에 질문이 연결되어 있는지 확인
        boolean hasRelatedQuestions = existingCategories.stream()
                .anyMatch(category -> questionCategoryRepository.hasRelatedQuestions(category.getId()));

        if (hasRelatedQuestions) {
            throw new CustomHttpException(HttpErrorCode.CONFLICT,
                    "기존 카테고리에 연결된 질문이 있어 초기화할 수 없습니다. " +
                    "먼저 모든 질문을 삭제하거나 다른 카테고리로 옮겨주세요.");
        }

        // 기존 카테고리 삭제
        existingCategories.forEach(questionCategoryRepository::delete);
        log.info("기존 카테고리 {} 개가 삭제되었습니다.", existingCategories.size());

        // 기본 카테고리 추가
        List<QuestionCategoryDTO> result = new ArrayList<>();
        for (QuestionCategoryCreateDTO categoryDTO : DEFAULT_CATEGORIES) {
            QuestionCategoryEntity entity = QuestionCategoryEntity.builder()
                    .name(categoryDTO.getName())
                    .description(categoryDTO.getDescription())
                    .build();

            QuestionCategoryEntity saved = questionCategoryRepository.save(entity);
            result.add(new QuestionCategoryDTO(saved.getId(), saved.getName(), saved.getDescription()));
        }

        log.info("기본 카테고리 {} 개가 초기화되었습니다.", result.size());
        return result;
    }
}
