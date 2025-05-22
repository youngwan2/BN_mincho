package com.mincho.herb.domain.dashboard.application;

import com.mincho.herb.domain.dashboard.dto.DashboardResponseDTO;

import java.time.LocalDateTime;

public interface DashboardService {

//    DashboardResponseDTO getDashboardSummary(LocalDateTime startDate, LocalDateTime endDate);
    DashboardResponseDTO getDashboardSummary();
}
