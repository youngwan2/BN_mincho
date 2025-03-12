package com.mincho.herb.domain.post.application.postStatistics;

import com.mincho.herb.common.config.error.HttpErrorCode;
import com.mincho.herb.common.exception.CustomHttpException;
import com.mincho.herb.domain.post.dto.PostCountDTO;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postStatistics.PostStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostStatisticsServiceImpl implements PostStatisticsService {

    private final PostStatisticsRepository postStatisticsRepository;

    @Override
    public List<PostCountDTO> getPostStatistics() {
        List<PostCountDTO> postCountDTOs = postStatisticsRepository.countsByCategory();

        if(postCountDTOs.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "");
        }
        return postCountDTOs;
    }
}
