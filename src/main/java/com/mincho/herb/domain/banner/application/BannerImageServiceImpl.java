package com.mincho.herb.domain.banner.application;

import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.AuthUtils;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * {@code BannerImageServiceImpl}는 배너 이미지를 Amazon S3에 업로드, 수정, 삭제하는 기능을 제공하는 서비스입니다.
 * {@link S3Service}를 활용하여 실제 파일 처리를 수행합니다.
 * <p>
 * S3의 경로는 고정된 "banner/images" 경로를 사용합니다.
 * </p>
 *
 * @author YoungWan Kim
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BannerImageServiceImpl implements BannerImageService {

    private final S3Service s3Service;
    private final AuthUtils authUtils;

    private static final String BANNER_IMAGE_PATH = "banner/images";

    /**
     * 배너 이미지를 S3에 업로드합니다.
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL
     */
    @Override
    public String uploadBannerImage(MultipartFile file) {
        if(!authUtils.hasAdminRole()){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너 이미지를 업로드할 권한이 없습니다."); 
        } 
        
        return s3Service.upload(file, BANNER_IMAGE_PATH);
    }

    /**
     * 기존 S3 이미지를 삭제한 후 새로운 이미지를 업로드합니다.
     *
     * @param url  기존 이미지의 S3 URL
     * @param file 새로 업로드할 이미지 파일
     * @return 새로 업로드된 이미지의 URL
     */
    @Override
    public String updateBannerImage(String url, MultipartFile file) {
        if(!authUtils.hasAdminRole()){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너 이미지를 수정할 권한이 없습니다.");
        }
        this.deleteBannerImage(url);
        return s3Service.upload(file, BANNER_IMAGE_PATH);
    }

    /**
     * S3에서 배너 이미지를 삭제합니다.
     *
     * @param url 삭제할 이미지의 S3 URL
     */
    @Override
    public void deleteBannerImage(String url) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "배너 이미지를 삭제할 권한이 없습니다.");
        }
        String s3Key = s3Service.extractKeyFromUrl(url);
        s3Service.deleteKey(s3Key);

        // 배너와 관련된 추가 리소스 삭제 로직
        log.info("배너 이미지와 관련된 리소스가 삭제되었습니다. Key: {}", s3Key);
    }

    /**
     * Presigned URL을 생성합니다.
     *
     * @param fileName   파일 이름
     * @param contentType 파일의 콘텐츠 유형
     * @param duration   URL의 유효 기간(초)
     * @return 생성된 presigned URL
     */
    @Override
    public String generatePresignedUrl(String fileName, String contentType, int duration) {
        if (!authUtils.hasAdminRole()) {
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST, "Presigned URL을 생성할 권한이 없습니다.");
        }
        String key = BANNER_IMAGE_PATH + "/" + fileName;
        return s3Service.generatePresignedUrl(key, contentType, duration);
    }
}
