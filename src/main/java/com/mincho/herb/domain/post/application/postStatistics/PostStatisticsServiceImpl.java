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

    private final PostRepository postRepository;

    @Override
    public List<PostCountDTO> getPostStatistics() {
        List<PostCountDTO> counts = postRepository.countsByCategory();
        if(counts.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회할 게시글 통계가 존재하지 않습니다.");
        }
        return counts;
    }
}
