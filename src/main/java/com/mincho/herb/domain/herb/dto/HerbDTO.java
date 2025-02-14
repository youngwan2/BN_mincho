package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HerbDTO {
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
    private String prvateTherpy; // 민간요법
    private String stle; // 형태
    private String useeRegn; // 이용 부위
}
