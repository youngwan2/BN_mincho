package com.mincho.herb.global.aop.userActivity;

import com.mincho.herb.global.entity.BaseCreatedAtEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_activity_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserActivityLogEntity extends BaseCreatedAtEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // 유저 이메일
    private String logType;
    private Long contentId; // 액션 내용 식별 ID(ex. herb_id, post_id 등)
    private String contentTitle;
    private String content; // 액션 식별 내용(ex. 약초 상세 페이지, 게시글 상세 페이지 등)
}
