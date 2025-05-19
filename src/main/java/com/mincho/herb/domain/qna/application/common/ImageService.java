package com.mincho.herb.domain.qna.application.common;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    List<String> getImages(Long qnaId);

    void imageUpload(List<MultipartFile> images, Long id);
    void imageUpdate(List<String> newImageUrls, Long id);



}
