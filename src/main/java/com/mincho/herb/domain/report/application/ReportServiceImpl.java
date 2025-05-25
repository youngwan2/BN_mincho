package com.mincho.herb.domain.report.application;

import com.mincho.herb.domain.report.dto.*;
import com.mincho.herb.domain.report.entity.ReportEntity;
import com.mincho.herb.domain.report.entity.ReportHandleStatusEnum;
import com.mincho.herb.domain.report.entity.ReportHandleTargetTypeEnum;
import com.mincho.herb.domain.report.entity.ReportResonSummaryEnum;
import com.mincho.herb.domain.report.repository.ReportRepository;
import com.mincho.herb.domain.user.application.user.UserService;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.exception.CustomHttpException;
import com.mincho.herb.global.io.EmailService;
import com.mincho.herb.global.response.error.HttpErrorCode;
import com.mincho.herb.global.util.CommonUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 신고(Report) 관련 비즈니스 로직을 처리하는 서비스 구현체입니다.
 *
 * <p>신고 생성, 조회, 처리 및 신고 리스트 조회 기능을 제공합니다.</p>
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final CommonUtils commonUtils;

    /**
     * 새로운 신고를 생성합니다.
     *
     * <p>로그인한 사용자만 신고를 생성할 수 있으며, 신고 정보는 {@link CreateReportRequestDTO} 로 전달받습니다.</p>
     *
     * @param requestDTO 신고 생성 요청 DTO
     * @return 생성된 {@link ReportEntity}
     * @throws CustomHttpException 로그인하지 않은 경우 {@code HttpErrorCode.UNAUTHORIZED_REQUEST} 발생
     */
    @Override
    @Transactional
    public ReportEntity createReport(CreateReportRequestDTO requestDTO){
        String email = commonUtils.userCheck();

        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }

        UserEntity userEntity = userService.getUserByEmail(email);

        return reportRepository.save(ReportEntity.builder()
                .targetId(requestDTO.getTargetId())
                .targetContentTitle(requestDTO.getTargetContentTitle())
                .targetContentUrl(requestDTO.getTargetContentUrl())
                .targetType(ReportHandleTargetTypeEnum.valueOf(requestDTO.getTargetType()))
                .status(ReportHandleStatusEnum.PENDING)
                .reasonSummary(ReportResonSummaryEnum.valueOf(requestDTO.getReasonSummary()))
                .reason(requestDTO.getReason())
                .reporter(userEntity)
                .build());
    }

    /**
     * 특정 신고를 ID로 조회합니다.
     *
     * <p>로그인한 사용자만 조회할 수 있습니다.</p>
     *
     * @param id 신고 ID
     * @return {@link ReportDTO} 조회된 신고 정보
     * @throws CustomHttpException 로그인하지 않은 경우 {@code HttpErrorCode.UNAUTHORIZED_REQUEST} 발생
     * @throws java.util.NoSuchElementException 해당 ID의 신고가 없는 경우
     */
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
                .targetType(reportEntity.getTargetType().name())
                .reasonSummary(reportEntity.getReasonSummary())
                .reason(reportEntity.getReason())
                .handleTitle(reportEntity.getHandleTitle())
                .handledAt(reportEntity.getHandledAt())
                .status(reportEntity.getStatus().name())
                .handleMemo(reportEntity.getHandleMemo())
                .handleTitle(reportEntity.getHandleTitle())
                .build();
    }

    /**
     * 신고 처리 작업을 수행하고, 처리 결과를 신고자에게 이메일로 알립니다.
     *
     * <p>로그인한 사용자만 신고 처리 가능하며, {@link HandleReportRequestDTO} 를 통해 처리 상태 및 내용을 입력받습니다.</p>
     *
     * @param reportId 처리할 신고의 ID
     * @param requestDTO 신고 처리 요청 DTO
     * @throws MessagingException 이메일 발송 중 오류 발생 시
     * @throws CustomHttpException 로그인하지 않은 경우 {@code HttpErrorCode.UNAUTHORIZED_REQUEST} 발생
     */
    @Override
    @Transactional
    public void handleReport(Long reportId, HandleReportRequestDTO requestDTO) throws MessagingException {
        String email = commonUtils.userCheck();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }

        UserEntity handler = userService.getUserByEmail(email); // 신고 처리자
        ReportEntity reportEntity = reportRepository.findById(reportId); // 신고 엔티티 조회

        reportEntity.setStatus(ReportHandleStatusEnum.valueOf(requestDTO.getStatus()));
        reportEntity.setHandleTitle(requestDTO.getHandleTitle());
        reportEntity.setHandleMemo(requestDTO.getHandleMemo());
        reportEntity.setHandledAt(LocalDateTime.now());
        reportEntity.setHandler(handler);

        requestDTO.setHandleAt(requestDTO.getHandleAt());

        sendReportHandleEmail(requestDTO,
                ReportDTO.builder()
                        .reporter(reportEntity.getReporter().getEmail())
                        .reasonSummary(reportEntity.getReasonSummary())
                        .build(),
                reportEntity.getReporter().getProfile().getNickname()
        );
    }

    /**
     * 신고 검색 조건과 페이징 정보를 기반으로 전체 신고 목록을 조회합니다.
     *
     * <p>관리자 권한이 있어야 접근할 수 있습니다.</p>
     *
     * @param keyword               검색어(키워드)
     * @param filteringConditionDTO 필터링 조건
     * @param reportSortDTO         정렬 조건
     * @param pageable              페이징 정보
     * @return {@link ReportsResponseDTO} 신고 목록 및 페이징 정보
     * @throws CustomHttpException 로그인하지 않았거나 관리자 권한이 없는 경우 {@code HttpErrorCode.UNAUTHORIZED_REQUEST}, {@code HttpErrorCode.FORBIDDEN_ACCESS} 발생
     */
    @Override
    @Transactional(readOnly = true)
    public ReportsResponseDTO getAllReports(String keyword, ReportFilteringConditionDTO filteringConditionDTO, ReportSortDTO reportSortDTO, Pageable pageable) {
        String email = commonUtils.userCheck();
        String role = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        if(email == null){
            throw new CustomHttpException(HttpErrorCode.UNAUTHORIZED_REQUEST,"로그인 후 이용해주세요.");
        }
        if(!role.equals("ROLE_ADMIN")){
            throw new CustomHttpException(HttpErrorCode.FORBIDDEN_ACCESS,"관리자만 접근할 수 있습니다.");
        }

        return reportRepository.searchReports(keyword, filteringConditionDTO, reportSortDTO,pageable);
    }

    /**
     * 신고 처리 결과를 신고자에게 이메일로 발송합니다.
     *
     * @param requestDTO 처리 내용이 포함된 {@link HandleReportRequestDTO}
     * @param reportDTO 신고 정보 DTO
     * @param username 신고자의 닉네임
     * @throws MessagingException 이메일 전송 실패 시
     */
    @Override
    public void sendReportHandleEmail(HandleReportRequestDTO requestDTO, ReportDTO reportDTO, String username) throws MessagingException {
        emailService.sendEmail(
                "[" + requestDTO.getHandleTitle() + "] 에 대한 신고 처리 결과를 알립니다.",
                "[처리일시: requestDTO.getHandleAt() +] " + requestDTO.getHandleMemo(),
                username,
                reportDTO.getReporter()
        );
    }
}
