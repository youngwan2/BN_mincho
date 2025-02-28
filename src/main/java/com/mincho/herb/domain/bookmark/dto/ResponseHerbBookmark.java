package com.mincho.herb.domain.bookmark.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ResponseHerbBookmark {
    private Long id;
    private String url;

    @Override
    public String toString() {
        return "ResponseFavoriteHerb{" +
                "id=" + id +
                ", url='" + url + '\'' +
                '}';
    }
}
