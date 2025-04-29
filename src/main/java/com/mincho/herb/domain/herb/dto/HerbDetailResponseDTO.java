package com.mincho.herb.domain.herb.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class HerbDetailResponseDTO {
    private Long id;
    private String cntntsNo; // 콘텐츠 번호
    private String bneNm; // 학명
    private String cntntsSj; // 제목
    private String hbdcNm; // 한방명
    private List<String> imgUrls; // 이미지 URL 배열
    private String prvateTherpy; // 민간요법
    private String stle; // 형태
    private String useeRegn; // 이용 부위
    private Long viewCount;
    private String growthForm; // 생장 형태
    private String flowering; // 개화기
    private String habitat;   // 분포 및 환경
    private String harvest;   // 수확·건조
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
