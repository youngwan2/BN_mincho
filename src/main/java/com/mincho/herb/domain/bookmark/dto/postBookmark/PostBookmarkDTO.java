package com.mincho.herb.domain.bookmark.dto.postBookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostBookmarkDTO {
    private Long postId;
    private String title;
    private String categoryName;
    private String authorName;
    private LocalDateTime bookmarkedAt;
}
