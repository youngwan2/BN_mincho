package com.mincho.herb.domain.bookmark.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HerbBookmarkLogResponseDTO {

    private Long herbId; // 약초 ID
    private String herbName; // 약초 이름
}
