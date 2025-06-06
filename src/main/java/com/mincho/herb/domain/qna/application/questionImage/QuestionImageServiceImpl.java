package com.mincho.herb.domain.qna.application.questionImage;

import com.mincho.herb.domain.qna.entity.QuestionEntity;
import com.mincho.herb.domain.qna.entity.QuestionImageEntity;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.qna.repository.questionImage.QuestionImageRepository;
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


/**
 * QuestionImageService 구현체로, Q&A 게시글에 첨부되는 이미지의 업로드, 조회, 수정, 삭제를 처리합니다.
 * 이미지 파일은 S3에 저장되며, 이미지 메타데이터는 DB에 저장됩니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionImageServiceImpl implements QuestionImageService {

    private final S3Service s3Service;
    private final QuestionImageRepository questionImageRepository;
    private final QuestionRepository questionRepository;
    private final AuthUtils authUtils;
    private static final int MAX_IMAGE_COUNT = 3;
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1MB
    private static final List<String> ALLOWED_EXTENSIONS = List.of(
            "jpg", "jpeg", "png", "webp", "svg"
    );

    /**
     * 특정 Q&A 게시글의 이미지 URL 리스트를 조회합니다.
     *
     * @param qnaId Q&A 게시글 ID
     * @return 이미지 URL 목록
     */
    @Override
    public List<String> getImages(Long qnaId) {
        return questionImageRepository.findAllImageUrlsByQnaId(qnaId);
    }

    /**
     * 이미지 파일을 업로드하고 DB에 저장합니다. (엔티티 직접 전달 버전)
     *
     * @param images    업로드할 이미지 파일 목록
     * @param questionEntity 이미지가 연결될 Q&A 게시글 엔티티
     */
    @Override
    public void imageUpload(List<MultipartFile> images, QuestionEntity questionEntity) {

        throwValidImageException(images);

        // 이미지 업로드(S3, DB)
        images.forEach(image -> {
            questionImageRepository.save(
                    QuestionImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "qna/" + questionEntity.getId()))
                            .question(questionEntity)
                            .build()
            );
        });
    }


    /**
     * 이미지 파일을 업로드하고 DB에 저장합니다. (ID만 전달되는 API용 버전)
     *
     * @param images 업로드할 이미지 파일 목록
     * @param qnaId  Q&A 게시글 ID
     */
    @Override
    @Transactional
    public void imageUpload(List<MultipartFile> images, Long qnaId) {
            throwValidImageException(images);

            QuestionEntity questionEntity = questionRepository.findById(qnaId);

            // 이미지 업로드(S3, DB)
            images.forEach(image -> {
            questionImageRepository.save(
                    QuestionImageEntity.builder()
                            .imageUrl(s3Service.upload(image, "qna/" + questionEntity.getId()))
                            .question(questionEntity)
                            .build()
            );
        });
    }

    /**
     * 전달받은 이미지 URL 목록에 해당하는 이미지들을 S3 및 DB에서 제거합니다.
     *
     * @param imageUrlsToDelete 삭제할 이미지 URL 목록
     * @param id                Q&A 게시글 ID
     */
    @Override
    @Transactional
    public void imageUpdate(List<String> imageUrlsToDelete, Long id) {
        throwAuthExceptionOrReturnEmail(); // 예외 처리

        // 전달받은 imageUrls가 null이거나 비어있으면 처리하지 않음
        if (imageUrlsToDelete == null || imageUrlsToDelete.isEmpty()) {
            return;
        }

        log.info("삭제 요청된 이미지 URL 목록: {}", imageUrlsToDelete);

        // DB와 S3에서 이미지 삭제
        questionImageRepository.deleteByImageUrlIn(imageUrlsToDelete);

        // S3에서 이미지 파일 삭제
        imageUrlsToDelete.forEach(imageUrl -> {
            String key = s3Service.extractKeyFromUrl(imageUrl);
            log.info("S3에서 삭제할 이미지 키: {}", key);
            s3Service.deleteKey(key);
        });
    }


    /**
     * Q&A 게시글에 연결된 모든 이미지를 삭제합니다.
     *
     * @param imageUrls 삭제할 이미지 URL 목록
     * @param questionEntity 삭제할 Q&A 게시글 엔티티
     */
    @Override
    @Transactional
    public void imageDelete(List<String> imageUrls, QuestionEntity questionEntity) {
        for(String imageUrl : imageUrls){
            String key = s3Service.extractKeyFromUrl(imageUrl);
            s3Service.deleteKey(key);
        }

        questionImageRepository.deleteByQnaId(questionEntity.getId());

    }


    /**
     * 로그인 여부를 확인하고 이메일을 반환합니다.
     *
     * @return 로그인된 사용자의 이메일
     * @throws CustomHttpException 로그인되어 있지 않은 경우 예외 발생
     */
    private String throwAuthExceptionOrReturnEmail(){
        String email = authUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용 가능합니다.");
        }
        return email;
    }

    /**
     * 업로드된 이미지 파일의 유효성을 검사합니다.
     *
     * @param images 이미지 파일 목록
     * @throws CustomHttpException 유효하지 않은 이미지가 포함된 경우 예외 발생
     */
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
