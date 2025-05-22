package com.mincho.herb.domain.dashboard.api;

import com.mincho.herb.domain.dashboard.application.DashboardService;
import com.mincho.herb.domain.dashboard.dto.DashboardResponseDTO;
import com.mincho.herb.domain.herb.dto.DailyHerbStatisticsDTO;
import com.mincho.herb.domain.post.dto.DailyPostStatisticsDTO;
import com.mincho.herb.domain.user.dto.DailyUserStatisticsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponseDTO> getDashboardSummary(){
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }

    @GetMapping("/summary/post")
    public ResponseEntity<List<DailyPostStatisticsDTO>> getDailyPostStatics(
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
            ){

        return ResponseEntity.ok(dashboardService.getDailyPostStatics(startDate, endDate));
    }

    @GetMapping("/summary/herb")
    public ResponseEntity<List<DailyHerbStatisticsDTO>> getDailyHerbStatics(
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ){
        return ResponseEntity.ok(dashboardService.getDailyHerbStatics(startDate, endDate));
    }


    @GetMapping("/summary/user-register")
    public ResponseEntity<List<DailyUserStatisticsDTO>> getDailyUserStatics(
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate
    ){
        return ResponseEntity.ok(dashboardService.getDailyUserStatics(startDate, endDate));
    }
}
