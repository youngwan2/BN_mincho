package com.mincho.herb.domain.bookmark.application;


public interface HerbBookmarkService {

    void removeHerbBookmark(Long favoriteHerbId);
    void addHerbBookmark(String url, Long herbId);
    Integer getBookmarkCount(Long herbId);
}
