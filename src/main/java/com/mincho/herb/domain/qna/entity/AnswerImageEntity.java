package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.AnswerImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answer_image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="answer_id", nullable = true)
    private AnswerEntity answer;

    // 엔티티로
    public static AnswerImageEntity toEntity(AnswerImage AnswerImage, AnswerEntity answerEntity){
        AnswerImageEntity AnswerImageEntity = new AnswerImageEntity();
        AnswerImageEntity.setId(AnswerImage.getId());
        AnswerImageEntity.setAnswer(answerEntity);
        AnswerImageEntity.setImageUrl(AnswerImage.getImageUrl());

        return AnswerImageEntity;
    }

    // 도메인 객체로
    public AnswerImage toDomain(){
        return AnswerImage.builder()
                .id(this.id)
                .imageUrl(this.imageUrl)
                .build();
    }
}
