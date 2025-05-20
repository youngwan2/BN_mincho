package com.mincho.herb.global.aop.userActivity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserActivityLogDTO {

    private String userId; // 유저 이메일
    private String logType;
    private Long contentId; // 내용 식별 ID(ex. herb_id, post_id 등)
    private String contentTitle;
    private String content; // 식별 내용(ex. 약초 상세 페이지, 게시글 상세 페이지 등)
}
