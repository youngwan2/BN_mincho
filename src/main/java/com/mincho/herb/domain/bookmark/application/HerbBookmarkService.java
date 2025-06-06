package com.mincho.herb.domain.bookmark.application;


import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkLogResponseDTO;
import com.mincho.herb.domain.bookmark.dto.herbBookmark.HerbBookmarkResponseDTO;
import org.springframework.data.domain.Pageable;

public interface HerbBookmarkService {
    void removeHerbBookmark(Long favoriteHerbId);
    HerbBookmarkLogResponseDTO addHerbBookmark(String url, Long herbId);
    Long getBookmarkCount(Long herbId);
    Boolean isBookmarked(Long herbId);
    HerbBookmarkResponseDTO getBookmarks(int page, int size);
    HerbBookmarkResponseDTO getBookmarksByUserId(Long userId, Pageable pageable);
}
