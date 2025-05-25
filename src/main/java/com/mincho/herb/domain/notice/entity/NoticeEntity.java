package com.mincho.herb.domain.notice.entity;

import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String category; // 점검, 업데이트 등

    private Boolean pinned; // 고정 여부

    private Boolean deleted;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notice_tags", joinColumns = @JoinColumn(name = "notice_id"))
    @Column(name = "tag")
    private List<String> tags;

    private LocalDateTime publishedAt;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private UserEntity admin;


}
