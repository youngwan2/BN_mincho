package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.repository.refreshToken.RefreshTokenRepository;
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
            String path = request.getRequestURI();
            if (path.startsWith("/login/oauth2/code") || path.startsWith("/oauth2/")) {
                filterChain.doFilter(request, response);
                return;
            }

            String accessToken = resolveToken(request);

            /* accessToken이 존재하는 경우*/
            if (accessToken != null) {
                
                // 토큰이 만료 되었는가?  NO! 토큰 만료 안 됨 유효한 토큰임
                if (!jwtAuthProvider.isExpiredToken(accessToken)) {
                 
                    String email = jwtAuthProvider.getEmail(accessToken);
                    logger.info("email: "+ email);

                    Collection<GrantedAuthority> authorities = jwtAuthProvider.getAuthorities(accessToken);
                    Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                    
                    // 사용자 정보 저장
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    logger.info("JwtAuthFilter 처리 성공 " + authentication.getAuthorities().iterator().next().getAuthority());
                } else {
                    // 액세스 토큰 재발급 요청
                    handleExpiredToken(request, response);
                }
            }
            filterChain.doFilter(request, response);

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
            handleException(response, "해당 요청을 처리할 권한이 없습니다.", HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // refreshToken 이 존재한다면 모두 가져오기
        List<Cookie> cookies = Arrays.stream(request.getCookies())
                .filter(cookie -> Objects.equals(cookie.getName(), "refresh"))
                .toList();

        // 리프레쉬 토큰이 존재한다면
        if (!cookies.isEmpty()) {
            String refreshToken = cookies.get(0).getValue();

            // 데이터베이스에 실제 저장되어 있는지
            if(refreshTokenRepository.findByRefreshToken(refreshToken) == null){
                handleException(response, "refreshToken이 유효하지 않습니다.", HttpServletResponse.SC_UNAUTHORIZED);
            }

            // 리프레쉬 토큰이 만료? true : false
            Boolean isValidRefreshToken = jwtAuthProvider.isExpiredToken(refreshToken);

            // refresh 토큰이 만료되지 않았다면 해당 토큰으로 사용자의 이메일(식별용) 정보를 가져온다.
            if (!isValidRefreshToken) {
                String email = jwtAuthProvider.getEmail(refreshToken);

                Collection<GrantedAuthority> authorities = jwtAuthProvider.getAuthorities(refreshToken);
                Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 새로운 accessToken 발급
                String newAccessToken = jwtAuthProvider.createAccessToken(email, authorities.iterator().next().getAuthority(), 60*60*10*1000L );

                logger.info("new token:"+ newAccessToken);


                // 새로운 accessToken을 헤더로 클라이언트에 전달
                response.setHeader("Authorization","Bearer "+newAccessToken);
                logger.info("refreshToken으로 accessToken 재발급 완료");

                // 리프레쉬 토큰이 만료 되었다면
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

    /** Bearer 와 토큰을 분리 후 토큰만 반환한다.*/
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.split(" ")[1];
        }
        return null;
    }
}