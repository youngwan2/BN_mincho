package com.mincho.herb.global.response.error;

import lombok.Getter;



@Getter
public enum HttpErrorCode {

    // 각 에러 타입을 사용하여 정의
    BAD_REQUEST(400, HttpErrorType.BAD_REQUEST, "Invalid request."),
    UNAUTHORIZED_REQUEST(401, HttpErrorType.UNAUTHORIZED, "Unauthorized."),
    FORBIDDEN_ACCESS(403, HttpErrorType.FORBIDDEN, "Forbidden."),
    RESOURCE_NOT_FOUND(404, HttpErrorType.NOT_FOUND, "Not found."),
    METHOD_NOT_ALLOWED(405, HttpErrorType.METHOD_NOT_ALLOWED, "Not allowed method."),
    CONFLICT(409, HttpErrorType.CONFLICT, "Conflict."),
    INTERNAL_SERVER_ERROR(500, HttpErrorType.INTERNAL_SERVER_ERROR, "Server error.");

    // Getter 메서드
    private final int httpStatus;         // HTTP 상태 코드
    private final HttpErrorType errorType;   // 에러 타입
    private final String message;        // 사용자 메시지

    HttpErrorCode(int httpStatus, HttpErrorType errorType, String message) {
        this.httpStatus = httpStatus;
        this.errorType = errorType;
        this.message = message;
    }

}
