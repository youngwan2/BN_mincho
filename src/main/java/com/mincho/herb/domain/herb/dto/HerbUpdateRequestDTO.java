package com.mincho.herb.domain.herb.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HerbUpdateRequestDTO {
    private String cntntsNo; // 콘텐츠 번호

    @NotEmpty(message = "학명은 필수입니다.")
    private String bneNm; // 학명

    @NotEmpty(message = "약초명은 필수입니다.")
    private String cntntsSj; // 제목

    @NotEmpty(message = "한방명은 필수입니다.")
    private String hbdcNm; // 한방명

    @NotEmpty(message = "민간요법 설명은 필수입니다.")
    private String prvateTherpy; // 민간요법

    @NotEmpty(message = "이용 부위는 필수입니다.")
    private String useeRegn; // 이용 부위

    private String growthForm; // 생장 형태
    private String flowering; // 개화기
    private String habitat;   // 분포 및 환경(재배환경)
    private String harvest;   // 수확·건조
}
