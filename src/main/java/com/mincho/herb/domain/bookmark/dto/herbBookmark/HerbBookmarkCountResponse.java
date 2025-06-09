package com.mincho.herb.domain.bookmark.dto.herbBookmark;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HerbBookmarkCountResponse {

    private Long count;
    private Boolean isBookmarked;
}
