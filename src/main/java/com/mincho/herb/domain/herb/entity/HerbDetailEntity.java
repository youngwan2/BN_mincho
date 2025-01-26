package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.common.base.BaseEntity;
import com.mincho.herb.domain.herb.domain.HerbDetail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "HerbDetail")
@AllArgsConstructor
@NoArgsConstructor
public class HerbDetailEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cntntsNo; // 콘텐츠 번호
    private String bneNm; // 학명
    private String cntntsSj; // 제목
    private String hbdcNm; // 한방명
    private String imgUrl1; // 추가 이미지 URL 1
    private String imgUrl2; // 추가 이미지 URL 2
    private String imgUrl3; // 추가 이미지 URL 3
    private String imgUrl4; // 추가 이미지 URL 4
    private String imgUrl5; // 추가 이미지 URL 5
    private String imgUrl6; // 추가 이미지 URL 6

    @Column(length = 2000)
    private String prvateTherpy; // 민간요법

    @Column(length = 1000)
    private String stle; // 형태
    private String useeRegn; // 이용 부위

    public static HerbDetailEntity toEntity(HerbDetail herbDetail) {
        HerbDetailEntity herbDetailEntity = new HerbDetailEntity();

        herbDetailEntity.cntntsNo = herbDetail.getCntntsNo();
        herbDetailEntity.bneNm = herbDetail.getBneNm();
        herbDetailEntity.cntntsSj = herbDetail.getCntntsSj();
        herbDetailEntity.hbdcNm = herbDetail.getHbdcNm();
        herbDetailEntity.imgUrl1 = herbDetail.getImgUrl1();
        herbDetailEntity.imgUrl2 = herbDetail.getImgUrl2();
        herbDetailEntity.imgUrl3 = herbDetail.getImgUrl3();
        herbDetailEntity.imgUrl4 = herbDetail.getImgUrl4();
        herbDetailEntity.imgUrl5 = herbDetail.getImgUrl5();
        herbDetailEntity.imgUrl6 = herbDetail.getImgUrl6();
        herbDetailEntity.prvateTherpy = herbDetail.getPrvateTherpy();
        herbDetailEntity.stle = herbDetail.getStle();
        herbDetailEntity.useeRegn = herbDetail.getUseeRegn();

        return herbDetailEntity;
    }

    public HerbDetail toModel() {
        return HerbDetail.builder()
                .cntntsNo(this.cntntsNo)
                .bneNm(this.bneNm)
                .cntntsSj(this.cntntsSj)
                .hbdcNm(this.hbdcNm)
                .imgUrl1(this.imgUrl1)
                .imgUrl2(this.imgUrl2)
                .imgUrl3(this.imgUrl3)
                .imgUrl4(this.imgUrl4)
                .imgUrl5(this.imgUrl5)
                .imgUrl6(this.imgUrl6)
                .prvateTherpy(this.prvateTherpy)
                .stle(this.stle)
                .useeRegn(this.useeRegn)
                .build();
    }
}
