package com.mincho.herb.domain.qna.application.qnaImage;

import com.mincho.herb.domain.qna.application.common.ImageService;
import com.mincho.herb.domain.qna.entity.QnaEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QnaImageService extends ImageService {

    void imageUpload(List<MultipartFile> images, QnaEntity qnaEntity);
    void imageDelete(List<String> images, QnaEntity qnaEntity);
}
