package com.mincho.herb.domain.herb.domain;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Herb {
    private Long id;
    private String cntntsNo; // 콘텐츠 번호
    private String bneNm; // 학명
    private String cntntsSj; // 제목
    private String hbdcNm; // 한방명
    private String imgUrl1; // 이미지 URL 1
    private String imgUrl2; // 이미지 URL 2
    private String imgUrl3; // 이미지 URL 3
    private String imgUrl4; // 이미지 URL 4
    private String imgUrl5; // 이미지 URL 5
    private String imgUrl6; // 이미지 URL 6
    private String prvateTherpy; // 민간요법
    private String useeRegn; // 이용 부위
    private String growthForm; // 생장형태
    private String flowering; // 개화기
    private String habitat; // 분포 및 환경
    private String harvest; // 수확·건조
    private Long adminId;
}
