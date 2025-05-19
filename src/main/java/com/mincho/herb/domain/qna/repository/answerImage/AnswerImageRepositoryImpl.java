package com.mincho.herb.domain.qna.repository.answerImage;

import com.mincho.herb.domain.qna.entity.AnswerImageEntity;
import com.mincho.herb.domain.qna.entity.QnaImageEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
@RequiredArgsConstructor
public class AnswerImageRepositoryImpl implements  AnswerImageRepository {
    
    private final AnswerImageJpaRepository answerImageJpaRepository;


    // 이미지 저장
    @Override
    public AnswerImageEntity save(AnswerImageEntity answerImageEntity) {
        return answerImageJpaRepository.save(answerImageEntity);
    }

    // 이미지 조회
    @Override
    public List<String> findAllImageUrlsByAnswerId(Long qnaId) {
        return answerImageJpaRepository.findAllByAnswerId(qnaId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "등록된 이미지를 찾을 수 없습니다.")).stream().map(QnaImageEntity::getImageUrl).toList();
    }

    @Override
    public void deleteByAnswerId(Long qnaId) {
        answerImageJpaRepository.deleteByAnswerId(qnaId);
    }


    // 리스트에 속하지 않는 이미지를 모두 제거
    @Override
    public void deleteByImageUrlIn(List<String> imageUrls) {

        answerImageJpaRepository.deleteByImageUrlIn(imageUrls);

    }
}
