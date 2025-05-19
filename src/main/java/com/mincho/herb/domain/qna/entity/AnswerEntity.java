package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.Answer;
import com.mincho.herb.domain.user.entity.MemberEntity;
import com.mincho.herb.global.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qna_id")
    private QnaEntity qna;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private MemberEntity writer;

    private Boolean isAdopted;

    @PrePersist
    public void prePersist() {
        this.isAdopted = false;
    }

    // 엔티티로
    public static AnswerEntity toEntity(Answer answer, QnaEntity qnaEntity, MemberEntity memberEntity){
        AnswerEntity answerEntity = new AnswerEntity();
            answerEntity.setId(answer.getId());
            answerEntity.setQna(qnaEntity);
            answerEntity.setWriter(memberEntity);
            answerEntity.setContent(answer.getContent());
            answerEntity.setIsAdopted(answer.getIsAdopted());
        return answerEntity;
    }

    // 도메인 객체로
    public Answer toDomain(){
        return Answer.builder()
                .id(this.id)
                .content(this.content)
                .qnaId(this.qna.getId())
                .writerId(this.writer.getId())
                .isAdopted(this.isAdopted)
                .createdAt(this.getCreatedAt())
                .updatedAt(this.getUpdatedAt())
                .build();
    }
}
