package com.mincho.herb.domain.qna.repository.qnaImage;

import com.mincho.herb.domain.qna.entity.QnaImageEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class QnaImageRepositoryImpl implements QnaImageRepository {

    private final QnaImageJpaRepository qnaImageJpaRepository;

    // 이미지 저장
    @Override
    public QnaImageEntity save(QnaImageEntity qnaImageEntity) {
        return qnaImageJpaRepository.save(qnaImageEntity);
    }

    // 이미지 조회
    @Override
    public List<String> findAllImageUrlsByQnaId(Long qnaId) {
        return qnaImageJpaRepository.findAllByQnaId(qnaId).orElseThrow(()-> new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "등록된 이미지를 찾을 수 없습니다.")).stream().map(QnaImageEntity::getImageUrl).toList();
    }

    // 이미지 삭제
    @Override
    public void deleteByQnaId(Long qnaId) {
        qnaImageJpaRepository.deleteByQnaId(qnaId);
    }


    // 리스트에 속하지 않는 이미지를 모두 제거
    @Override
    public void deleteByImageUrlIn(List<String> imageUrls) {

        qnaImageJpaRepository.deleteByImageUrlIn(imageUrls);

    }
}
