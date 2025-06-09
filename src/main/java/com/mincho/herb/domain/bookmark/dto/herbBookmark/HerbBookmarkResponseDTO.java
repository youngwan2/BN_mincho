package com.mincho.herb.domain.bookmark.dto.herbBookmark;

import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class HerbBookmarkResponseDTO {
    private Long count;
    private List<HerbBookmark> bookmarks;
}
