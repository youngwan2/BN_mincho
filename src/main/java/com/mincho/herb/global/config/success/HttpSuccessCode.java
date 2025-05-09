package com.mincho.herb.global.config.success;

import lombok.Getter;

@Getter
public enum HttpSuccessCode {

    // 성공 상태 코드 정의
    OK(200, HttpSuccessType.OK, "Request successful."),
    CREATED(201, HttpSuccessType.CREATED, "Resource created successfully."),
    ACCEPTED(202, HttpSuccessType.ACCEPTED, "Request accepted for processing."),
    NO_CONTENT(204, HttpSuccessType.NO_CONTENT, "No content to send for this request.");

    private final int httpStatus;          // HTTP 상태 코드
    private final HttpSuccessType successType; // 성공 유형
    private final String message;          // 사용자 메시지

    HttpSuccessCode(int httpStatus, HttpSuccessType successType, String message) {
        this.httpStatus = httpStatus;
        this.successType = successType;
        this.message = message;
    }


}
