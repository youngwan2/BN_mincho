package com.mincho.herb.global.response.success;

public enum HttpSuccessType {
        OK,                 // 요청이 성공적으로 처리됨
        CREATED,            // 리소스가 성공적으로 생성됨
        ACCEPTED,           // 요청이 처리될 예정
        NO_CONTENT;         // 응답할 내용이 없음
}
