package com.mincho.herb.domain.dashboard.application;

import com.mincho.herb.domain.dashboard.dto.DashboardResponseDTO;
import com.mincho.herb.domain.herb.dto.DailyHerbStatisticsDTO;
import com.mincho.herb.domain.herb.dto.HerbStatisticsDTO;
import com.mincho.herb.domain.herb.repository.herb.HerbAdminRepository;
import com.mincho.herb.domain.post.dto.DailyPostStatisticsDTO;
import com.mincho.herb.domain.post.dto.PostStatisticsDTO;
import com.mincho.herb.domain.post.repository.post.PostRepository;
import com.mincho.herb.domain.post.repository.postStatistics.PostStatisticsRepository;
import com.mincho.herb.domain.qna.repository.question.QuestionRepository;
import com.mincho.herb.domain.report.dto.ReportStatisticsDTO;
import com.mincho.herb.domain.report.repository.ReportRepository;
import com.mincho.herb.domain.report.repository.ReportStatisticsRepository;
import com.mincho.herb.domain.user.dto.DailyUserStatisticsDTO;
import com.mincho.herb.domain.user.dto.UserStatisticsDTO;
import com.mincho.herb.domain.user.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService{

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostStatisticsRepository postStatisticsRepository;
    private final QuestionRepository questionRepository;
    private final ReportRepository reportRepository;
    private final ReportStatisticsRepository reportStatisticsRepository;
    private final HerbAdminRepository herbAdminRepository;

    // 대시보드 통계 요약
    @Override
    public DashboardResponseDTO getDashboardSummary() {

        return DashboardResponseDTO.builder()
                .userStatistics(getUserStatistics())
                .postStatistics(getPostStatistics())
                .herbStatistics(getHerbStatistics())
                .reportStatistics(getReportStatistics())
                .build();
    }


    // 게시글 통계(일별)
    @Override
    public List<DailyPostStatisticsDTO> getDailyPostStatics(LocalDate startDate, LocalDate endDate) {
        return postStatisticsRepository.findDailyPostStatistics(startDate, endDate);
    }

    // 약초 통계(일별)
    @Override
    public List<DailyHerbStatisticsDTO> getDailyHerbStatics(LocalDate startDate, LocalDate endDate) {
        return herbAdminRepository.findDailyHerbStatistics(startDate, endDate);
    }

    // 유저 통계(월별)
    @Override
    public List<DailyUserStatisticsDTO> getDailyUserStatics(LocalDate startDate, LocalDate endDate) {
        return userRepository.findDailyRegisterStatistics(startDate, endDate);
    }


    // 유저 통계
    private UserStatisticsDTO getUserStatistics(){
        return userRepository.findUserStatics();
    }

    // 게시글 통계
    private PostStatisticsDTO getPostStatistics(){
        return  postStatisticsRepository.findPostStatics();
    }

    // QnA 통계

    // 약초 통계
    private HerbStatisticsDTO getHerbStatistics(){
        return herbAdminRepository.findHerbStatics();
    }

    // 신고 통계
    private ReportStatisticsDTO getReportStatistics(){
        return reportStatisticsRepository.findReportStatics();
    }

}
