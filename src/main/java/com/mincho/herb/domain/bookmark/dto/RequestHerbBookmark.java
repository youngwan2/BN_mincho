package com.mincho.herb.domain.bookmark.dto;


import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class RequestHerbBookmark {
    @NotEmpty(message = "url은 필수입니다.")
    private String url;
}
