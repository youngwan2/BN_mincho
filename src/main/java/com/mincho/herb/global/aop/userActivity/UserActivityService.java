package com.mincho.herb.global.aop.userActivity;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivityService {

    private final UserActivityJpaRepository userActivityJpaRepository;

    /**
     * 유저 활동 로그를 저장하는 메서드 입니다.
     * @param logDto 사용자 활동 로그 저장 DTO
     */
    public void saveLog(UserActivityLogDTO logDto){

        UserActivityLogEntity logEntity = UserActivityLogEntity.builder()
                .userId(logDto.getUserId())
                .logType(logDto.getLogType())
                .contentId(logDto.getContentId())
                .contentTitle(logDto.getContentTitle())
                .content(logDto.getContent())
                .build();

        userActivityJpaRepository.save(logEntity);

    }
}
