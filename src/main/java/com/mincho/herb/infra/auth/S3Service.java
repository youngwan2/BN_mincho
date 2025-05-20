package com.mincho.herb.infra.auth;

import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AWS S3 관련 기능을 제공하는 서비스 클래스
 * 이미지 업로드, 프리사인드 URL 생성, 이미지 삭제, 이미지 URL 추출 등의 기능을 수행
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${spring.cloud.aws.s3.bucketName}")
    private String bucket;

    /**
     * MultipartFile을 받아 AWS S3에 업로드하고 접근 가능한 정적 URL을 반환
     *
     * @param file 업로드할 이미지 파일
     * @param path S3 버킷 내 저장 경로
     * @return 업로드된 파일의 S3 URL
     * @throws RuntimeException 파일 업로드 중 IO 예외가 발생할 경우
     */
    public String upload(MultipartFile file, String path){
        try {
            String key = path +"/"+ UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            return "https://" + bucket + ".s3.amazonaws.com/" + key;
        } catch (IOException ex){
            throw new RuntimeException("S3 업로드 실패", ex);
        }
    }

    /**
     * 업로드할 이미지 파일에 대해 S3 프리사인드 URL을 생성합
     * 파일은 유효성 검사 후, 주어진 유효 시간(duration) 동안 유효한 PUT presigned URL을 반환
     *
     * @param file     업로드할 이미지 파일
     * @param duration URL 유효 시간(분 단위)
     * @return presigned URL 문자열
     * @throws CustomHttpException 파일 유효성 검사 실패 시
     */
    public String generatePresignedUrl(MultipartFile file, int duration){

        if(file.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"파일이 누락되었습니다.");
        }

        String contentType = file.getContentType();
        long fileSize = file.getSize();
        String fileName = file.getOriginalFilename();

        if (contentType == null || !contentType.startsWith("image/") || fileSize > 5 * 1024 * 1024) {
            throw new CustomHttpException(HttpErrorCode.BAD_REQUEST, "잘못된 형식의 이미지 파일 입니다.");
        }

        String key = "post/images/" + UUID.randomUUID() + "_" + fileName;

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        return s3Presigner.presignPutObject(p -> p
                .signatureDuration(Duration.ofMinutes(duration))
                .putObjectRequest(objectRequest)
        ).url().toString();
    }

    /**
     * S3 버킷에서 주어진 파일 키에 해당하는 객체를 삭제
     *
     * @param fileName 삭제할 S3 객체의 key (예: "post/images/uuid_filename.jpg")
     */
    public void deleteKey(String fileName){
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();

        s3Client.deleteObject(request);
    }

    // 이미지 목록에서 안 쓰는 이미지를 S3에서 제거
    public void deleteRemovedOldImages(String oldContent, String newContent) {
        List<String> oldImages = extractImageUrls(oldContent);
        List<String> newImages = extractImageUrls(newContent);
        oldImages.removeAll(newImages);

        for (String url : oldImages) {
            deleteKey(extractKeyFromUrl(url));
        }
    }

    // content 에서 읽어온 모든 이미지 삭제
    public void deleteAllImagesInContent(String content) {
        extractImageUrls(content).forEach(url -> {
            String s3Key = extractKeyFromUrl(url);
            deleteKey(s3Key);
        });
    }



    /**
     * HTML 컨텐츠에서 이미지의 src 속성을 정규표현식을 사용해 모두 추출
     *
     * @param content HTML 형식의 문자열
     * @return src 속성 값 리스트 (이미지 URL 목록)
     */
    public List<String> extractImageUrls(String content){
        List<String> imageUrls = new ArrayList<>();
        String regex =  "<img[^>]+src=[\"']?([^\"'>\\s]+)[\"']?";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()){
            imageUrls.add(matcher.group(1));
        }

        return imageUrls;
    }

    /**
     * 전체 S3 URL에서 Key (객체 경로)만 추출
     * 예: "https://my-bucket.s3.amazonaws.com/posts/abc.jpg" → "posts/abc.jpg"
     *
     * @param url 전체 S3 URL
     * @return S3 Key 문자열
     * @throws CustomHttpException 잘못된 URL 형식일 경우
     */
    public String extractKeyFromUrl(String url){
        String target = ".amazonaws.com/";

        int index = url.indexOf(target);
        if(index == -1) throw new CustomHttpException(HttpErrorCode.BAD_REQUEST,"잘못된 형식의 S3 URL");

        String[] parts = url.split(target);

        return parts[1];
    }
}
