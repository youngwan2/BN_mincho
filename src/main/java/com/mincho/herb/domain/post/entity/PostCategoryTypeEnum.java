package com.mincho.herb.domain.post.entity;

import lombok.Getter;

/**
 * 게시글 카테고리 타입 (제한된 값과 설명 포함)
 */
@Getter
public enum PostCategoryTypeEnum {
    NOTICE("공지사항", "주요 공지사항"),
    DAILY("일상 이야기", "약초와 관련된 일상, 소소한 이야기, 자유 수다"),
    EXPERIENCE("약초 경험담", "복용 후 효과, 부작용, 민간요법 등의 실제 경험 공유"),
    INFO("정보 공유", "책, 방송, 기사, 유튜브 등에서 얻은 약초 관련 정보 공유"),
    CULTIVATION("채집/재배 팁", "직접 채집한 경험, 재배 방법, 계절별 관리 팁 공유"),
    CAUTION("부작용/주의사항", "복용 후 부작용 사례, 주의해야 할 약초 공유"),
    EVENT("이벤트/모임", "오프라인 약초 모임, 산행 정보, 커뮤니티 이벤트 등"),
    ETC("자유 주제", "기타 카테고리에 속하지 않는 이야기들");

    private final String category;
    private final String description;
    PostCategoryTypeEnum(String category, String description) {
        this.category = category;
        this.description = description;
    }
}
