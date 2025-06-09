package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.user.entity.UserEntity;
import com.mincho.herb.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer_reaction", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "answer_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerReactionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id")
    private AnswerEntity answer;

    @Column(nullable = false)
    private ReactionType reactionType;

    public enum ReactionType {
        LIKE, DISLIKE
    }
}
