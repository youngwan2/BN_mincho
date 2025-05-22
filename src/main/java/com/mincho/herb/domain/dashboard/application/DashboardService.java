package com.mincho.herb.domain.dashboard.application;

import com.mincho.herb.domain.dashboard.dto.DashboardResponseDTO;
import com.mincho.herb.domain.herb.dto.DailyHerbStatisticsDTO;
import com.mincho.herb.domain.post.dto.DailyPostStatisticsDTO;
import com.mincho.herb.domain.user.dto.DailyUserStatisticsDTO;

import java.time.LocalDate;
import java.util.List;

public interface DashboardService {

//    DashboardResponseDTO getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate);
    DashboardResponseDTO getDashboardSummary();

    List<DailyPostStatisticsDTO> getDailyPostStatics(LocalDate startDate, LocalDate endDate);

    List<DailyHerbStatisticsDTO> getDailyHerbStatics(LocalDate startDate, LocalDate endDate);

    List<DailyUserStatisticsDTO> getDailyUserStatics(LocalDate startDate, LocalDate endDate);
}
