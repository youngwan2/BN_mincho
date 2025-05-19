package com.mincho.herb.domain.qna.entity;

import com.mincho.herb.domain.qna.domain.QnaImage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "QnaImage")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QnaImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="qna_id", nullable = false)
    private QnaEntity qna;

    // 엔티티로
    public static QnaImageEntity toEntity(QnaImage qnaImage, QnaEntity qnaEntity){
        QnaImageEntity qnaImageEntity = new QnaImageEntity();
            qnaImageEntity.setId(qnaImage.getId());
            qnaImageEntity.setQna(qnaEntity);
            qnaImageEntity.setImageUrl(qnaImage.getImageUrl());

        return qnaImageEntity;
    }
    
    // 도메인 객체로
    public QnaImage toDomain(){
        return QnaImage.builder()
                .id(this.id)
                .imageUrl(this.imageUrl)
                .build();
    }
}
