package com.mincho.herb.domain.post.repository.postStatistics;

import com.mincho.herb.domain.post.dto.PostCountDTO;

import java.util.List;

public interface PostStatisticsRepository {
    List<PostCountDTO> countsByCategory();
}
