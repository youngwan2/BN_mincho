package com.mincho.herb.domain.favorite.domain;

import com.mincho.herb.domain.herb.domain.HerbSummary;
import com.mincho.herb.domain.user.domain.User;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Pattern;

@Builder
@Data
public class FavoriteHerb {
    private Long id;
    private String url;
    private User user;
    private HerbSummary herbSummary;

    public static boolean isValidUrl(String url){
        String URL_REGEX = "^(http://|https://).*";
        Pattern pattern = Pattern.compile(URL_REGEX);

        return pattern.matcher(url).matches();
    }

}
