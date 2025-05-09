package com.mincho.herb.domain.herb.entity;

import com.mincho.herb.global.base.BaseEntity;
import com.mincho.herb.domain.herb.domain.Herb;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Herbs")
@EqualsAndHashCode(callSuper = false)
public class HerbEntity extends BaseEntity {

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

    @Column(length = 5000)
    private String prvateTherpy; // 민간요법

    private String useeRegn; // 이용 부위

    private Long adminId;

    @Column(length = 1500)
    private String growthForm; // 생장형태

    private String flowering; // 개화기

    private String habitat; // 분포 및 환경

    private String harvest; // 수확·건조


    // 엔티티로
    public static HerbEntity toEntity(Herb herb) {
        HerbEntity herbEntity = new HerbEntity();
        herbEntity.id = herb.getId();
        herbEntity.cntntsNo = herb.getCntntsNo();
        herbEntity.bneNm = herb.getBneNm();
        herbEntity.cntntsSj = herb.getCntntsSj();
        herbEntity.hbdcNm = herb.getHbdcNm();
        herbEntity.imgUrl1 = herb.getImgUrl1();
        herbEntity.imgUrl2 = herb.getImgUrl2();
        herbEntity.imgUrl3 = herb.getImgUrl3();
        herbEntity.imgUrl4 = herb.getImgUrl4();
        herbEntity.imgUrl5 = herb.getImgUrl5();
        herbEntity.imgUrl6 = herb.getImgUrl6();
        herbEntity.prvateTherpy = herb.getPrvateTherpy();
        herbEntity.useeRegn = herb.getUseeRegn();
        herbEntity.growthForm = herb.getGrowthForm();
        herbEntity.flowering = herb.getFlowering();
        herbEntity.habitat = herb.getHabitat();
        herbEntity.harvest = herb.getHarvest();

        return herbEntity;
    }

    // 도메인으로
    public Herb toModel() {
        return Herb.builder()
                .id(this.id)
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
                .useeRegn(this.useeRegn)
                .growthForm(this.growthForm)
                .flowering(this.flowering)
                .habitat(this.habitat)
                .harvest(this.harvest)
                .build();
    }
}
