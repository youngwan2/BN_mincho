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
public class HerbCreateRequestDTO {
    private String cntntsNo; // 콘텐츠 번호

    @NotEmpty(message = "학명은 필수입니다.")
    private String bneNm; // 학명

    @NotEmpty(message = "약초명은 필수입니다.")
    private String cntntsSj; // 제목

    @NotEmpty(message = "한방명은 필수입니다.")
    private String hbdcNm; // 한방명

    private String imgUrl1; // 추가 이미지 URL 1
    private String imgUrl2; // 추가 이미지 URL 2
    private String imgUrl3; // 추가 이미지 URL 3
    private String imgUrl4; // 추가 이미지 URL 4
    private String imgUrl5; // 추가 이미지 URL 5
    private String imgUrl6; // 추가 이미지 URL 6

    @NotEmpty(message = "민간요법 설명은 필수입니다.")
    private String prvateTherpy; // 민간요법

    @NotEmpty(message = "약초의 형태 설명은 필수입니다.")
    private String stle; // 형태

    @NotEmpty(message = "약초의 이용 부위 설명은 필수입니다.")
    private String useeRegn; // 이용 부위
}
