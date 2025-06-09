package com.mincho.herb.domain.bookmark.dto.postBookmark;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PostBookmarkResponseDTO {
    private List<PostBookmarkDTO> bookmarks;
    private Long totalCount; // 북마크 개수
}
