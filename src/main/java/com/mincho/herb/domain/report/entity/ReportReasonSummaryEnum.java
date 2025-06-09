package com.mincho.herb.domain.report.entity;

import lombok.Getter;

/**
 * 신고 사유 요약 Enum 클래스.
 * 사용자가 신고할 수 있는 대표적인 8가지 사유를 정의한다.
 */
@Getter
public enum ReportReasonSummaryEnum {

    INAPPROPRIATE_CONTENT("부적절한 콘텐츠"),
    SPAM("스팸"),
    ABUSIVE_LANGUAGE("욕설/비방"),
    MISINFORMATION("잘못된 정보"),
    MISCONDUCT("부적절한 행동"),
    COPYRIGHT_INFRINGEMENT("저작권 침해"),
    PRIVACY_VIOLATION("개인정보 노출"),
    ADVERTISEMENT("광고/홍보"),
    ETC("기타");

    private final String description;

    ReportReasonSummaryEnum(String description) {
        this.description = description;
    }
}
