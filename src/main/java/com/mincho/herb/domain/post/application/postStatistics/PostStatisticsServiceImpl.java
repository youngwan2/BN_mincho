package com.mincho.herb.domain.post.application.postStatistics;

import com.mincho.herb.domain.post.dto.PostCategoryInfoDTO;
import com.mincho.herb.domain.post.repository.postStatistics.PostStatisticsRepository;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.response.error.HttpErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostStatisticsServiceImpl implements PostStatisticsService {

    private final PostStatisticsRepository postStatisticsRepository;

    // 포스트 통계
    @Override
    public List<PostCategoryInfoDTO> getPostStatistics() {
        List<PostCategoryInfoDTO> counts = postStatisticsRepository.countsByCategory();
        if(counts.isEmpty()){
            throw new CustomHttpException(HttpErrorCode.RESOURCE_NOT_FOUND, "조회할 게시글 통계가 존재하지 않습니다.");
        }
        return counts;
    }
}
