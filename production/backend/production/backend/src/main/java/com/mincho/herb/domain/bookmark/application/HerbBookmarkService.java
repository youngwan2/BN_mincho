package com.mincho.herb.domain.bookmark.application;


import com.mincho.herb.domain.bookmark.dto.HerbBookmarkResponseDTO;

public interface HerbBookmarkService {
    void removeHerbBookmark(Long favoriteHerbId);
    void addHerbBookmark(String url, Long herbId);
    Integer getBookmarkCount(Long herbId);
    Boolean isBookmarked(Long herbId);
    HerbBookmarkResponseDTO getBookmarks(int page, int size);


}
