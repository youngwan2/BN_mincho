package com.mincho.herb.domain.herb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HerbAdminDTO {
    private Long id;
    private String bneNm; // 학명
    private String cntntsSj; // 제목(약초명)
    private String hbdcNm; // 한방명
    private String imgUrl1;
    private String imgUrl2;
    private String imgUrl3;
    private String imgUrl4;
    private String imgUrl5;
    private String imgUrl6;
    private String prvateTherpy; // 민간 요법
    private String useeRegn; // 이용 부위
    private String growthForm; // 생장 형태
    private String flowering; // 개화기
    private String habitat; // 분포 및 환경
    private String harvest; // 수확 및 건조
    private Long viewCount; // 조회 수
    private List<TagDTO> tags;
}
