package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.Question;
import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEntity extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private QuestionCategoryEntity category;

    @Builder.Default
    private Long view = 0L ;

    @ElementCollection
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Question 도메인 객체 → 엔티티로 변환
     */
    public static QuestionEntity toEntity(Question question, UserEntity writer, QuestionCategoryEntity category) {
        return QuestionEntity.builder()
                .id(question.getId())
                .title(question.getTitle())
                .content(question.getContent())
                .isPrivate(question.getIsPrivate())
                .writer(writer)
                .category(category)
                .view(question.getView())
                .tags(question.getTags())
                .build();
    }

    /**
     * Question 엔티티 → 도메인 객체로 변환
     */
    public Question toDomain() {
        return Question.builder()
                .id(this.id)
                .title(this.title)
                .content(this.content)
                .isPrivate(this.isPrivate)
                .writerId(this.writer.getId())
                .categoryId(this.category != null ? this.category.getId() : null)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .view(this.view)
                .tags(this.tags)
                .build();
    }
}
