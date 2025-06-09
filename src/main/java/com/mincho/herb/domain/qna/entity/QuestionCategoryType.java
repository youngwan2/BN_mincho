package com.mincho.herb.domain.qna.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionCategoryType {
    HERB_USAGE("약초 활용법", "약초의 다양한 활용 방법에 대한 질문"),
    HERB_IDENTIFICATION("약초 식별", "약초 종류 식별 및 특성에 관한 질문"),
    HERB_CULTIVATION("약초 재배", "약초 재배 방법 및 환경에 관한 질문"),
    HERB_EFFECT("약초 효능", "약초의 효능 및 약리적 특성에 관한 질문"),
    HERB_RECIPE("약초 레시피", "약초를 활용한 요리, 차, 팅크 등의 레시피 관련 질문"),
    HERB_MEDICINE("약초 약용", "약초의 약용 활용에 관한 질문"),
    OTHER("기타", "기타 약초 관련 질문");

    private final String name;
    private final String description;
}
