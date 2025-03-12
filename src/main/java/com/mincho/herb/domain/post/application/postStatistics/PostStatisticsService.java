package com.mincho.herb.domain.post.application.postStatistics;

import com.mincho.herb.domain.post.dto.PostCountDTO;

import java.util.List;

public interface PostStatisticsService {

    List<PostCountDTO> getPostStatistics();
}
