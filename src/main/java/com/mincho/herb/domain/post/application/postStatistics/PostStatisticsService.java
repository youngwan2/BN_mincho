package com.mincho.herb.domain.post.application.postStatistics;

import com.mincho.herb.domain.post.dto.PostCategoryInfoDTO;

import java.util.List;

public interface PostStatisticsService {

    List<PostCategoryInfoDTO> getPostStatistics();
}
