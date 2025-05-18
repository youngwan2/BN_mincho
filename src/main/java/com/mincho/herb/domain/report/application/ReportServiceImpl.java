package com.mincho.herb.domain.report.application;

import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.repository.ReportRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.global.config.error.HttpErrorCode;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.io.EmailService;
import com.mincho.herb.global.util.CommonUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final CommonUtils commonUtils;


    // 신고하기
    @Override
    @Transactional
    public ReportEntity createReport(CreateReportRequestDTO requestDTO){

        String email = commonUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }

        MemberEntity memberEntity = userService.getUserByEmail(email);


        return reportRepository.save(ReportEntity.builder()
                .targetId(requestDTO.getTargetId())
                .targetType(requestDTO.getTargetType())
                .status(ReportHandleStatusEnum.PENDING)
                .reasonSummary(requestDTO.getReasonSummary())
                .reason(requestDTO.getReason())
                .reporter(memberEntity)
                .build());
    }

    // 신고 단건 조회
    @Override
    public ReportDTO getReport(Long id) {
        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }

        ReportEntity reportEntity = reportRepository.findById(id);

        return ReportDTO.builder()
                .id(reportEntity.getId())
                .reporter(reportEntity.getReporter().getEmail())
                .targetId(reportEntity.getTargetId())
                .targetType(reportEntity.getTargetType())
                .reasonSummary(reportEntity.getReasonSummary())
                .reason(reportEntity.getReason())
                .handleTitle(reportEntity.getHandleTitle())
                .handledAt(reportEntity.getHandledAt())
                .status(reportEntity.getStatus().name())
                .handleMemo(reportEntity.getHandleMemo())
                .handleTitle(reportEntity.getHandleTitle())
                .build();
    }

    // 신고 처리하기
    @Override
    @Transactional
    public void handleReport(Long reportId, HandleReportRequestDTO requestDTO) throws MessagingException {
        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }

        MemberEntity handler = userService.getUserByEmail(email);
        ReportEntity reportEntity = reportRepository.findById(reportId);

        reportEntity.setStatus(ReportHandleStatusEnum.valueOf(requestDTO.getStatus()));
        reportEntity.setHandleTitle(requestDTO.getHandleTitle()); // 처리 제목
        reportEntity.setHandleMemo(requestDTO.getHandleMemo()); // 처리 내용
        reportEntity.setHandledAt(LocalDateTime.now()); // 처리 일시
        reportEntity.setHandler(handler);

        requestDTO.setHandleAt(requestDTO.getHandleAt()); // 유저에게 전달할 처리일시 설정

        sendReportHandleEmail(requestDTO, ReportDTO.builder()
                .reporter(reportEntity.getReporter().getEmail())
                .reasonSummary(reportEntity.getReasonSummary())
                .build(),
                reportEntity.getReporter().getProfile().getNickname()
                );

        reportRepository.save(reportEntity);

    }

    // 전체 신고 리스트 조회
    @Override
    public ReportsResponseDTO getAllReports(ReportSearchConditionDTO reportSearchConditionDTO, Pageable pageable) {
        String email = commonUtils.userCheck();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }
        if(!role.equals("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"관리자만 접근할 수 있습니다.");
        }

        return reportRepository.searchReports(reportSearchConditionDTO, pageable);
    }

    // 신고 처리 메시지 알림
    @Override
    public void sendReportHandleEmail(HandleReportRequestDTO requestDTO, ReportDTO reportDTO, String username) throws MessagingException {

        emailService.sendEmail(
                "["+requestDTO.getHandleTitle() + "] 에 대한 신고 처리 결과를 알립니다." ,
                "[처리일시: requestDTO.getHandleAt() +] "+"+requestDTO.getHandleMemo()",
                username,
                reportDTO.getReporter()
        );

    }
}
