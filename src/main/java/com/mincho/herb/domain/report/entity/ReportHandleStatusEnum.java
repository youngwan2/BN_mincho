package com.mincho.herb.domain.report.entity;

import lombok.Getter;

@Getter
public enum ReportHandleStatusEnum {
    PENDING("대기"),
    RESOLVED("처리완료"),
    REJECTED("반려");

    private final String description;

    ReportHandleStatusEnum(String description) {
        this.description = description;
    }

}
