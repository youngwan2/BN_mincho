package com.mincho.herb.domain.bookmark.dto;

import com.mincho.herb.domain.bookmark.domain.HerbBookmark;
import lombok.*;

import java.util.List;


@Data
@Builder
public class HerbBookmarkResponseDTO {
    private Integer count;
    private List<HerbBookmark> bookmarks;
}
