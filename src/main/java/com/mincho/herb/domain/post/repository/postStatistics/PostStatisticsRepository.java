package com.mincho.herb.domain.post.repository.postStatistics;

import com.mincho.herb.domain.post.dto.DailyPostStatisticsDTO;
import com.mincho.herb.domain.post.dto.PostCategoryInfoDTO;
import com.mincho.herb.domain.post.dto.PostStatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface PostStatisticsRepository {
    List<PostCategoryInfoDTO> countsByCategory();
    PostStatisticsDTO findPostStatics();
    Long countByUserId(Long userId);
    List<DailyPostStatisticsDTO> findDailyPostStatistics(LocalDate startDate, LocalDate endDate);
}
