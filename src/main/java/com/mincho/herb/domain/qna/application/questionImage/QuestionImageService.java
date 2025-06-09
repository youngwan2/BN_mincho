package com.mincho.herb.domain.qna.application.questionImage;

import com.mincho.herb.domain.qna.application.common.ImageService;
import com.mincho.herb.domain.qna.entity.QuestionEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionImageService extends ImageService {

    void imageUpload(List<MultipartFile> images, QuestionEntity questionEntity);
    void imageDelete(List<String> images, QuestionEntity questionEntity);
}
