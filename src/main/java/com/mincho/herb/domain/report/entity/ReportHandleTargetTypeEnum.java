package com.mincho.herb.domain.report.entity;


import lombok.Getter;

@Getter
public enum ReportHandleTargetTypeEnum {
    USER("사용자"),
    POST("게시물"),
    POST_COMMENT("댓글"),
    QNA("질문"),
    QNA_REPLY("답변"),
    QNA_REPLY_COMMENT("답변 댓글"),
    HERB_CONTENT("약초 콘텐츠"),
    HERB_CONTENT_RECOMMEND_CHAT("AI 약초추천 챗"),
    NOTICE("공지사항");


    private final String description;

    ReportHandleTargetTypeEnum(String description) {
        this.description = description;
    }
}
