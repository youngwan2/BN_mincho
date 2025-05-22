package com.mincho.herb.domain.dashboard.dto;

import com.mincho.herb.domain.herb.dto.HerbStatisticsDTO;
import com.mincho.herb.domain.post.dto.PostStatisticsDTO;
import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.user.dto.UserStatisticsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponseDTO {
    private UserStatisticsDTO userStatistics; // 유저 통계

    private PostStatisticsDTO postStatistics; // 게시글 통계

    private HerbStatisticsDTO herbStatistics; // 약초 통계

    private ReportStatisticsDTO reportStatistics; // 신고 통계

}