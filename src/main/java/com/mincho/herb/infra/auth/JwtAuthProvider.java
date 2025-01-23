package com.mincho.herb.infra.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthProvider {

    private final JWTUtils jwtUtils;

    // JWT 생성
    public String generateToken(Authentication authentication, long jwtExp) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String email = userDetails.getUsername();

        return jwtUtils.createJwt(email, role, jwtExp);
    }

    // JWT 토큰 검증
    public boolean checkToken(String token){
        return jwtUtils.isExpired(token);
    }

    // 유저 이메일
    public String getEmail(String token){
        return jwtUtils.getEmail(token);
    }

    // 유저 역할
    public String getRole(String token){
        return jwtUtils.getRole(token);
    }
}
