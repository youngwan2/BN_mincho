package com.mincho.herb.infra.auth;

import com.mincho.herb.common.util.JWTUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JWTUtils jwtUtils;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails customOAuth2User = (CustomUserDetails) authentication.getPrincipal();
        String email = customOAuth2User.getUsername();

        String accessToken = jwtUtils.createJwt(email, "ROLE_USER",60 * 60 * 10 * 1000L); // 10분
        String refreshToken = jwtUtils.createJwt(email, "ROLE_USER",60 * 60 * 24 * 30 * 1000L); // 30일

        // Refresh Token은 HttpOnly 쿠키에 저장
        Cookie refreshCookie = new Cookie("refresh", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // HTTPS 환경에서만
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 14); // 2주
        response.addCookie(refreshCookie);

        String redirectUrl = "https://www.minchoherb.com/auth/oauth-success?token=" + accessToken;
        response.sendRedirect(redirectUrl);
    }
}

