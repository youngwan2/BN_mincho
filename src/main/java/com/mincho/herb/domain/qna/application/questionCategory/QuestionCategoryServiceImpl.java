package com.mincho.herb.domain.qna.application.questionCategory;

import com.mincho.herb.domain.qna.dto.QuestionCategoryDTO;
import com.mincho.herb.domain.qna.entity.QuestionCategoryEntity;
import com.mincho.herb.domain.qna.entity.QuestionCategoryType;
import com.mincho.herb.domain.qna.repository.questionCategory.QuestionCategoryJpaRepository;
import com.mincho.herb.domain.qna.repository.questionCategory.QuestionCategoryRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionCategoryServiceImpl implements QuestionCategoryService {

    private final QuestionCategoryRepository questionCategoryRepository;
    private final QuestionCategoryJpaRepository questionCategoryJpaRepository;
    private final AuthUtils authUtils;

    /**
     * 모든 QnA 카테고리 목록을 조회합니다.
     *
     * @return 카테고리 DTO 목록
     */
    @Override
    @Transactional(readOnly = true)
    public List<QuestionCategoryDTO> getAllCategories() {
        return questionCategoryRepository.findAll().stream()
                .map(category -> new QuestionCategoryDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 카테고리를 기본값으로 초기화합니다.
     * 이미 카테고리가 존재하면 무시하고, 없는 카테고리만 추가합니다.
     *
     * @return 초기화된 카테고리 DTO 목록
     */
    @Override
    @Transactional
    public List<QuestionCategoryDTO> initializeCategories() {

        boolean isAdmin = authUtils.hasAdminRole();

        String email = authUtils.userCheck();
        log.info("카테고리 초기화 요청: 이메일={}, 관리자 여부={}", email, isAdmin);


        if (!isAdmin) {
            log.warn("관리자 권한이 없는 사용자 요청: 카테고리 초기화");
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS ,"관리자 권한이 필요합니다.");
        }

        log.info("카테고리 초기화 시작");

        // enum에 정의된 모든 카테고리 타입을 조회하여 저장
        List<QuestionCategoryEntity> categoriesToSave = Arrays.stream(QuestionCategoryType.values())
                .map(type -> {
                    // 이미 존재하는지 확인
                    List<QuestionCategoryEntity> existingCategories = questionCategoryJpaRepository.findByName(type.getName())
                            .map(List::of)
                            .orElse(List.of());

                    // 이미 존재하는 카테고리가 있으면 null 반환
                    if (!existingCategories.isEmpty()) {
                        log.info("이미 존재하는 카테고리: {}", type.getName());
                        return null;
                    }

                    return QuestionCategoryEntity.builder()
                            .name(type.getName())
                            .description(type.getDescription())
                            .build();
                })
                .filter(Objects::nonNull) // null이 아닌 카테고리만 필터링
                .collect(Collectors.toList());

        // 신규 카테고리가 있으면 저장
        if (!categoriesToSave.isEmpty()) {
            log.info("{} 개의 새 카테고리 저장", categoriesToSave.size());
            questionCategoryJpaRepository.saveAll(categoriesToSave);
        }

        // 전체 카테고리 조회하여 반환
        return getAllCategories();
    }
}
