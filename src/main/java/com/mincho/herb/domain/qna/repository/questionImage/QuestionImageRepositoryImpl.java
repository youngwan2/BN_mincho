package com.mincho.herb.domain.qna.repository.questionImage;

import com.mincho.herb.domain.qna.entity.QuestionImageEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QuestionImageRepositoryImpl implements QuestionImageRepository {

    private final QuestionImageJpaRepository questionImageJpaRepository;

    // 이미지 저장
    @Override
    public QuestionImageEntity save(QuestionImageEntity questionImageEntity) {
        return questionImageJpaRepository.save(questionImageEntity);
    }

    // 이미지 조회
    @Override
    public List<String> findAllImageUrlsByQnaId(Long qnaId) {
        return questionImageJpaRepository.findAllByQuestionId(qnaId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "등록된 이미지를 찾을 수 없습니다.")).stream().map(QuestionImageEntity::getImageUrl).toList();
    }

    // 이미지 삭제
    @Override
    public void deleteByQnaId(Long qnaId) {
        questionImageJpaRepository.deleteByQnaId(qnaId);
    }


    // 리스트에 속하지 않는 이미지를 모두 제거
    @Override
    public void deleteByImageUrlIn(List<String> imageUrls) {

        questionImageJpaRepository.deleteByImageUrlIn(imageUrls);

    }
}
