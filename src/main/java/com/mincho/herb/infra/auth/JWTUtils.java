package com.mincho.herb.infra.auth;

import io.jsonwebtoken.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtils {

    private final SecretKey secretKey; // JWT 토큰 보안을 위한 비밀 키

    public JWTUtils(@Value("${spring.jwt.secret}") String secret) {
        // 비밀 키를 UTF-8 형식으로 변환하여 SecretKey 객체 생성
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // JWT 생성 메소드
    public String createJwt(String email, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("email", email) // 사용자 이름 클레임 추가
                .claim("role", role)         // 역할 클레임 추가
                .issuedAt(new Date(System.currentTimeMillis())) // 발급 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey) // 비밀 키로 서명
                .compact(); // JWT 문자열 생성
    }

    // JWT에서 email을 추출
    public String getEmail(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
    }

    // JWT에서 role을 추출
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // JWT의 만료 여부 확인
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }
}