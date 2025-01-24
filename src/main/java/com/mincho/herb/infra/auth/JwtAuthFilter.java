package com.mincho.herb.infra.auth;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthProvider jwtAuthProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = resolveToken(request);
            if (token != null && !jwtAuthProvider.checkToken(token)) {
                  String email = jwtAuthProvider.getEmail(token);
                  Collection<GrantedAuthority> collection = jwtAuthProvider.getAuthorities(token);
                  Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, collection);
                  SecurityContextHolder.getContext().setAuthentication(authentication);

                  logger.info("JwtAuthFilter 처리 성공 "+ authentication.getAuthorities().iterator().next().getAuthority());
            }
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            // JWT 만료 예외 처리
            handleException(response, "로그인 가능 시간이 만료되었습니다.. 다시 로그인해 주세요.", HttpServletResponse.SC_UNAUTHORIZED);
        } catch (Exception ex) {
            // 기타 예외 처리
            logger.error("JwtAuthFilter: "+ ex.getMessage() );
            handleException(response, "요청을 처리하는 중 오류가 발생했습니다.", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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
