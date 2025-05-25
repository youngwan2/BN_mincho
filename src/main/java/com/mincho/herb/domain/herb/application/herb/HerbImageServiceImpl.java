package com.mincho.herb.domain.herb.application.herb;

import com.mincho.herb.domain.herb.repository.herb.HerbRepository;
import com.mincho.herb.infra.auth.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HerbImageServiceImpl implements HerbImageService {
    private final S3Service s3Service;
    private final HerbRepository herbRepository;

    public List<String> uploadHerbImages(List<MultipartFile> imageFiles, Long herbId) {
        return imageFiles.stream().map(file -> s3Service.upload(file, "herbs"+"/"+herbId)).collect(Collectors.toList());
    }

    public void deleteHerbImages(List<String> urls) {
        urls.forEach(url -> {
            String key = s3Service.extractKeyFromUrl(url);
            s3Service.deleteKey(key);
        });
    }

    @Override
    public List<String> findHerbImageUrlsByHerbId(Long herbId) {
        return herbRepository.findHerbImagesByHerbId(herbId);
    }
}
