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


// 토큰 검증 필터
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

            if (accessToken != null) {
                // 토큰이 만료 되었는가?
                if (!jwtAuthProvider.checkToken(accessToken)) {
                    // NO! 토큰 만료 안 됨 유효한 토큰임
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
            logger.info("tokenExpiredError: "+ ex.getMessage());
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

            Boolean isValidRefreshToken = jwtAuthProvider.checkToken(refreshToken);

            // refresh 토큰이 만료되지 않았다면 해당 토큰으로 사용자의 이메일(식별용) 정보를 가져온다.
            if (!isValidRefreshToken) {
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
        response.getWriter().write(String.format("{\"message\": \"%s\"}", message));
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }
}