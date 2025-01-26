package com.mincho.herb.domain.herb.domain;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@Getter
public class HerbSummary {
    private String cntntsNo; // 콘텐츠 번호
    private String bneNm; // 학명
    private String cntntsSj; // 제목
    private String hbdcNm; // 한방명
    private String imgUrl; // 이미지 URL
    private String thumbImgUrl; // 썸네일 이미지 URL


}
