package com.mincho.herb.domain.bookmark.domain;

import com.mincho.herb.domain.herb.domain.Herb;
import com.mincho.herb.domain.user.domain.Member;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Pattern;

@Builder
@Data
public class HerbBookmark {
    private Long id;
    private String url;

    public static boolean isValidUrl(String url){
        String URL_REGEX = "^(http://|https://).*";
        Pattern pattern = Pattern.compile(URL_REGEX);

        return pattern.matcher(url).matches();
    }

}
