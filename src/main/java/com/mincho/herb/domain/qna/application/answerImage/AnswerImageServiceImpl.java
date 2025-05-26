package com.mincho.herb.domain.qna.application.answerImage;


import com.mincho.herb.domain.qna.entity.AnswerEntity;
import com.mincho.herb.domain.qna.entity.AnswerImageEntity;
import com.mincho.herb.domain.qna.repository.answer.AnswerRepository;
import com.mincho.herb.domain.qna.repository.answerImage.AnswerImageRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
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
public class AnswerImageServiceImpl implements  AnswerImageService {
    private final S3Service s3Service;
    private final AnswerRepository answerRepository;
    private final AnswerImageRepository answerImageRepository;
    private final AuthUtils authUtils;
    private static final int MAX_IMAGE_COUNT = 3;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "webp", "svg"
    );

    // 이미지 조회
    @Override
    public List<String> getImages(Long answerId) {
        return answerImageRepository.findAllImageUrlsByAnswerId(answerId);
    }

    // 이미지 업로드
    @Override
    public void imageUpload(List<MultipartFile> images, AnswerEntity answerEntity) {
        if(images == null) {return;} // 이미지 업로드 시도 자체가 없으면 그냥 탈출
        
        
        throwValidImageException(images);

        // 이미지 업로드(S3, DB)
        images.forEach(image -> {
            answerImageRepository.save(
                    AnswerImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "answer/" + answerEntity.getId()))
                            .answer(answerEntity)
                            .build()
            );
        });
    }


    // 이미지 업로드(API 전용)
    @Override
    @Transactional
    public void imageUpload(List<MultipartFile> images, Long answerId) {

        if(images == null) {return;}


        throwValidImageException(images);

        AnswerEntity answerEntity = answerRepository.findById(answerId);

        // 이미지 업로드(S3, DB)
        images.forEach(image -> {
            answerImageRepository.save(
                    AnswerImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "qna/" + answerEntity.getId()))
                            .answer(answerEntity)
                            .build()
            );
        });
    }

    // 이미지 수정
    @Override
    @Transactional
    public void imageUpdate(List<String> newImageUrls, Long id) {
        throwAuthExceptionOrReturnEmail(); // 예외 처리

        List<String> prevImageUrls = answerImageRepository.findAllImageUrlsByAnswerId(id); // 이전 이미지

        // ex [ 1, 2, 3, 4] - [3, 4] = [1, 2] -> prevImageUrls 에는 제거해야 할 이미지만 남는다.
        prevImageUrls.removeAll(newImageUrls);

        // 빈 게 아니라면 DB, S3 버킷에 반영
        if (!prevImageUrls.isEmpty()) {
            answerImageRepository.deleteByImageUrlIn(prevImageUrls);

            prevImageUrls.forEach(targetUrl-> {
                String key = s3Service.extractKeyFromUrl(targetUrl);
                s3Service.deleteKey(key);
            });
        }
    }


    // 이미지 삭제
    @Override
    @Transactional
    public void imageDelete(List<String> imageUrls, AnswerEntity answerEntity) {
        for(String imageUrl : imageUrls){
            String key = s3Service.extractKeyFromUrl(imageUrl);
            s3Service.deleteKey(key);
        }

        answerImageRepository.deleteByAnswerId(answerEntity.getId());

    }


    // 유저 체크(성공 시 유저 이메일 반환)
    private String throwAuthExceptionOrReturnEmail(){
        String email = authUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용 가능합니다.");
        }
        return email;
    }

    // 이미지 유효성 체크
    private void throwValidImageException(List<MultipartFile> images){
        if(images == null) { return;}

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
