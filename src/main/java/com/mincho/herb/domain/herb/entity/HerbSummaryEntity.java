package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.herb.domain.HerbSummary;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "HerbSummary")
@AllArgsConstructor
@NoArgsConstructor
public class HerbSummaryEntity extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String cntntsNo; // 콘텐츠 번호
        private String bneNm; // 학명
        private String cntntsSj; // 제목
        private String hbdcNm; // 한방명
        private String imgUrl; // 이미지 URL
        private String thumbImgUrl; // 썸네일 이미지 URL



    public static HerbSummaryEntity toEntity(HerbSummary herbSummary){
        HerbSummaryEntity herbSummaryEntity = new HerbSummaryEntity();
        herbSummaryEntity.cntntsNo = herbSummary.getCntntsNo();
        herbSummaryEntity.bneNm = herbSummary.getBneNm();
        herbSummaryEntity.cntntsSj = herbSummary.getCntntsSj();
        herbSummaryEntity.hbdcNm = herbSummary.getHbdcNm();
        herbSummaryEntity.imgUrl = herbSummary.getImgUrl();
        herbSummaryEntity.thumbImgUrl = herbSummary.getThumbImgUrl();

        return herbSummaryEntity;
    }

    public HerbSummary toModel(){
        return HerbSummary.builder()
                .cntntsNo(this.cntntsNo)
                .bneNm(this.bneNm)
                .cntntsSj(this.cntntsSj)
                .hbdcNm(this.hbdcNm)
                .imgUrl(this.imgUrl)
                .thumbImgUrl(this.thumbImgUrl)
                .build();
    }
}
