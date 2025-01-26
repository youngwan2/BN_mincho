package com.mincho.herb.common.util;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtils {

    // 쿠키 생성
    public Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        //cookie.setSecure(true); // 활성화 시 HTTS 에서만
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

}
