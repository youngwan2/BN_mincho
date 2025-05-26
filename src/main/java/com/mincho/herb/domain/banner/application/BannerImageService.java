package com.mincho.herb.domain.banner.application;

import org.springframework.web.multipart.MultipartFile;

public interface BannerImageService {
    String uploadBannerImage(MultipartFile file);
    String updateBannerImage(String key, MultipartFile file);
    void deleteBannerImage(String key);
    String generatePresignedUrl(String fileName, String contentType, int duration);
}
