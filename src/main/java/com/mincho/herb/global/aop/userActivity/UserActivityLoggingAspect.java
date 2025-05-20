package com.mincho.herb.global.aop.userActivity;

import com.mincho.herb.domain.herb.dto.HerbDetailResponseDTO;
import com.mincho.herb.domain.like.dto.LikeHerbResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class UserActivityLoggingAspect {
    private final UserActivityService userActivityService;

    @Around("@annotation(userActivityAction)")
    public Object loggingUserActivity(ProceedingJoinPoint joinPoint, UserActivityAction userActivityAction) throws Throwable {
        Object result = joinPoint.proceed(); // 메서드 실행

        // 유저 정보 추출
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String logType = userActivityAction.action();

        // 상세 페이지 조회 로그 저장
        switch (logType) {
            case "herb_detail" -> {
                if (result instanceof HerbDetailResponseDTO) {
                    Long herbId = ((HerbDetailResponseDTO) result).getId(); // 약초ID
                    String contentTitle = ((HerbDetailResponseDTO) result).getCntntsSj(); // 약초명

                    userActivityService.saveLog(
                            UserActivityLogDTO.builder()
                                    .userId(email)
                                    .logType(logType)
                                    .contentId(herbId)
                                    .contentTitle(contentTitle)
                                    .content("약초 상세 페이지 조회")
                                    .build()
                    );
                }
            }
            case "ai_recommendation" -> {
                String question = (String) joinPoint.getArgs()[0]; // 질문

                userActivityService.saveLog(
                        UserActivityLogDTO.builder()
                                .userId(email)
                                .logType(logType)
                                .contentTitle(question)
                                .content("사용자 증상 기반 약초 추천 챗봇")
                                .build()
                );
            }
            case "herb_like" -> {
                if (result instanceof LikeHerbResponseDTO) {
                    Long herbId = ((LikeHerbResponseDTO) result).getHerbId();
                    String contentTitle = ((LikeHerbResponseDTO) result).getHerbName();

                    userActivityService.saveLog(
                            UserActivityLogDTO.builder()
                                    .userId(email)
                                    .logType(logType)
                                    .contentId(herbId)
                                    .contentTitle(contentTitle)
                                    .content("약초 좋아요 ")
                                    .build()
                    );
                }
            }
            case "herb_bookmark" -> {
                if (result instanceof HerbDetailResponseDTO) {
                    Long herbId = ((HerbDetailResponseDTO) result).getId(); // 약초ID
                    String contentTitle = ((HerbDetailResponseDTO) result).getCntntsSj(); // 약초명

                    userActivityService.saveLog(
                            UserActivityLogDTO.builder()
                                    .userId(email)
                                    .logType(logType)
                                    .contentId(herbId)
                                    .contentTitle(contentTitle)
                                    .content("약초 북마크")
                                    .build()
                    );
                }
            }
        }
        return result;
    }
}
