package com.mincho.herb.domain.qna.application.qna;

import com.mincho.herb.domain.qna.entity.QnaEntity;
import com.mincho.herb.domain.qna.entity.QnaImageEntity;
import com.mincho.herb.domain.qna.repository.qnaImage.QnaImageRepository;
import com.mincho.herb.domain.qna.repository.qna.QnaRepository;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.util.CommonUtils;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class QnaImageServiceImpl implements  QnaImageService {

    private final S3Service s3Service;
    private final QnaImageRepository qnaImageRepository;
    private final QnaRepository qnaRepository;
    private final CommonUtils commonUtils;
    private static final int MAX_IMAGE_COUNT = 3;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "webp", "svg"
    );

    // 이미지 조회
    @Override
    public List<String> getImages(Long qnaId) {
        return qnaImageRepository.findAllImageUrlsByQnaId(qnaId);
    }

    // 이미지 업로드
    @Override
    public void imageUpload(List<MultipartFile> images, QnaEntity qnaEntity) {

        throwValidImageException(images);

        // 이미지 업로드(S3, DB)
        images.forEach(image -> {
            qnaImageRepository.save(
                    QnaImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "qna/" + qnaEntity.getId()))
                            .qna(qnaEntity)
                            .build()
            );
        });
    }


    // 이미지 업로드(API 전용)
    @Override
    @Transactional
    public void imageUpload(List<MultipartFile> images, Long qnaId) {
            throwValidImageException(images);

            QnaEntity qnaEntity = qnaRepository.findById(qnaId);

            // 이미지 업로드(S3, DB)
            images.forEach(image -> {
            qnaImageRepository.save(
                    QnaImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "qna/" + qnaEntity.getId()))
                            .qna(qnaEntity)
                            .build()
            );
        });
    }

    // 이미지 수정
    @Override
    @Transactional
    public void imageUpdate(List<String> newImageUrls, Long id) {
        throwAuthExceptionOrReturnEmail(); // 예외 처리
        
        List<String> prevImageUrls = qnaImageRepository.findAllImageUrlsByQnaId(id); // 이전 이미지

        // ex [ 1, 2, 3, 4] - [3, 4] = [1, 2] -> prevImageUrls 에는 제거해야 할 이미지만 남는다.
        prevImageUrls.removeAll(newImageUrls);

        // 빈 게 아니라면 DB, S3 버킷에 반영
        if (!prevImageUrls.isEmpty()) {
            qnaImageRepository.deleteByImageUrlIn(prevImageUrls);
            
            prevImageUrls.forEach(targetUrl-> {
                String key = s3Service.extractKeyFromUrl(targetUrl);
                s3Service.deleteKey(key);
            });
        }
    }


    // 이미지 삭제
    @Override
    @Transactional
    public void imageDelete(List<String> imageUrls, QnaEntity qnaEntity) {
        for(String imageUrl : imageUrls){
            String key = s3Service.extractKeyFromUrl(imageUrl);
            s3Service.deleteKey(key);
        }

        qnaImageRepository.deleteByQnaId(qnaEntity.getId());

    }


    // 유저 체크(성공 시 유저 이메일 반환)
    private String throwAuthExceptionOrReturnEmail(){
        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용 가능합니다.");
        }
        return email;
    }
    
    // 이미지 유효성 체크
    private void throwValidImageException(List<MultipartFile> images){
        if(!images.isEmpty()){
            if (images.size() > MAX_IMAGE_COUNT) {
                throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "이미지는 최대 3개까지만 업로드할 수 있습니다.");
            }


            for (MultipartFile image : images) {
                if (image.isEmpty()) {
                    throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"빈 파일은 업로드할 수 없습니다.");
                }

                if (image.getSize() > MAX_FILE_SIZE) {
                    throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"각 이미지 파일 크기는 1MB 이하여야 합니다.");
                }

                log.info("image type:{}", image.getContentType());
                if (!ALLOWED_EXTENSIONS.contains(Objects.requireNonNull(image.getContentType()).split("/")[1])) {
                    throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"지원하지 않는 이미지 형식입니다. (JPEG, PNG, webp, svg 만 허용)");
                }
            }
        }
    }
}
