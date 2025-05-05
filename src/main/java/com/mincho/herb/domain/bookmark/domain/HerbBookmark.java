package com.mincho.herb.domain.bookmark.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Builder
@Data
public class HerbBookmark {
    private Long id;
    private String cntntsSj;
    private String bneNm;
    private String hbdcNm;
    private String url;
    private LocalDateTime createdAt;

    public static boolean isValidUrl(String url){
        String URL_REGEX = "^(http://|https://).*";
        Pattern pattern = Pattern.compile(URL_REGEX);

        return pattern.matcher(url).matches();
    }

}
