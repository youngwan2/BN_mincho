package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.QuestionCategory;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionCategoryEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    /**
     * QuestionCategory 엔티티 → 도메인 객체로 변환
     */
    public QuestionCategory toDomain() {
        return QuestionCategory.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .build();
    }
}
