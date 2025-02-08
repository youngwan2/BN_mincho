package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.repository.refreshToken.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthProvider jwtAuthProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String accessToken = resolveToken(request);
            logger.info("jwtauthfilter accessToken:"+ accessToken);
            if (accessToken != null) {
                if (!jwtAuthProvider.checkToken(accessToken)) {
                    // accessToken이 유효한 경우
                    String email = jwtAuthProvider.getEmail(accessToken);
                    logger.info("email: "+ email);
                    Collection<GrantedAuthority> authorities = jwtAuthProvider.getAuthorities(accessToken);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("JwtAuthFilter 처리 성공 " + authentication.getAuthorities().iterator().next().getAuthority());
                }
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            // JWT 만료 예외 처리
            handleExpiredToken(request, response);
        } catch (Exception ex) {
            // 기타 예외 처리
            logger.error("JwtAuthFilter: " + ex.getMessage());
            handleException(response, "요청을 처리하는 중 오류가 발생했습니다.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 액세스 토큰 만료 시 재발급 처리
    private void handleExpiredToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 클라이언트에서 refreshToken을 쿠키나 헤더로 가져오기
        if(request.getCookies() == null) {
            handleException(response, "조회할 쿠키를 찾을 수 없습니다.", HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        List<Cookie> cookies = Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(cookie.getName(), "refresh"))
                .toList();

        if (!cookies.isEmpty()) {
            String refreshToken = cookies.get(0).getValue();

            if(refreshTokenRepository.findByRefreshToken(refreshToken) == null){
                handleException(response, "refreshToken이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            }

            if (jwtAuthProvider.checkToken(refreshToken)) {
                String email = jwtAuthProvider.getEmail(refreshToken);

                // 새로운 accessToken 발급
                String newAccessToken = jwtAuthProvider.createAccessToken(email, 60*60*10*1000L);

                // 새로운 accessToken으로 인증
                Collection<GrantedAuthority> authorities = jwtAuthProvider.getAuthorities(newAccessToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 새로운 accessToken을 헤더로 클라이언트에 전달
                response.setHeader("Authorization","Bearer "+newAccessToken);

                logger.info("JwtAuthFilter - refreshToken으로 accessToken 재발급 완료");
            } else {
                handleException(response, "refreshToken이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            handleException(response, "refreshToken이 없습니다.", HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleException(HttpServletResponse response, String message, int statusCode) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }
}