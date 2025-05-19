package com.mincho.herb.domain.qna.application.answer;

import com.mincho.herb.domain.qna.application.common.ImageService;
import com.mincho.herb.domain.qna.entity.AnswerEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AnswerImageService extends ImageService {
    void imageUpload(List<MultipartFile> images, AnswerEntity answerEntity);
    void imageDelete(List<String> images, AnswerEntity answerEntity);
}
