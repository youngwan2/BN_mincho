package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.Qna;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "qna")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QnaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Boolean isPrivate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private UserEntity writer;

    @Builder.Default
    private Long view = 0L ;

    /**
     * Qna 도메인 객체 → 엔티티로 변환
     */
    public static QnaEntity toEntity(Qna qna, UserEntity writer) {
        return QnaEntity.builder()
                .id(qna.getId())
                .title(qna.getTitle())
                .content(qna.getContent())
                .isPrivate(qna.getIsPrivate())
                .writer(writer)
                .view(qna.getView())
                .build();
    }

    /**
     * Qna 엔티티 → 도메인 객체로 변환
     */
    public Qna toDomain() {
        return Qna.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .isPrivate(this.isPrivate)
                .writerId(this.writer.getId())
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .view(this.view)
                .build();
    }
}
