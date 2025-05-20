package com.mincho.herb.infra.auth;

import com.mincho.herb.global.util.JWTUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

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

    /** 만료된 JWT 토큰인지  검증 | 만료 되면 true 를 반환함 */
    public Boolean isExpiredToken(String token){
        try {
            return jwtUtils.isExpired(token); // 만료 되었니?
        } catch (Exception e) {
            log.error("토큰 검증 중 오류 발생: {}", e.getMessage());
            return true; // 오류 발생 시 만료된 것으로 간주
        }
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

    // 만료 토큰 재생성
    public String createAccessToken(String email,String role, Long jwtExp) {
        return jwtUtils.createJwt(email, role, jwtExp);
    }
}
