package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.QuestionImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_image")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QuestionImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="question_id", nullable = false)
    private QuestionEntity question;

    // 엔티티로
    public static QuestionImageEntity toEntity(QuestionImage questionImage, QuestionEntity questionEntity){
        QuestionImageEntity questionImageEntity = new QuestionImageEntity();
            questionImageEntity.setId(questionImage.getId());
            questionImageEntity.setQuestion(questionEntity);
            questionImageEntity.setImageUrl(questionImage.getImageUrl());

        return questionImageEntity;
    }
    
    // 도메인 객체로
    public QuestionImage toDomain(){
        return QuestionImage.builder()
                .id(this.id)
                .imageUrl(this.imageUrl)
                .build();
    }
}
