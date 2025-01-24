package com.mincho.herb.infra.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
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
        log.info("checkToken: {}", token);
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

  public Collection<GrantedAuthority> getAuthorities(String token) {
        // JWT에서 권한 정보 추출
        String role = this.getRole(token);

        // GrantedAuthority로 변환
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new SimpleGrantedAuthority(role));

        return collection;
    }
}
