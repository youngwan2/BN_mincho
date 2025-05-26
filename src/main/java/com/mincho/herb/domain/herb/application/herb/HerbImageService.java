package com.mincho.herb.domain.herb.application.herb;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HerbImageService {
    List<String> uploadHerbImages(List<MultipartFile> imageFiles, Long herbId);
    void deleteHerbImages(List<String> urls);
    List<String> findHerbImageUrlsByHerbId(Long herbId);
}
