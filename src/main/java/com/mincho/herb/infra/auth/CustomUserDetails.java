package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final Member member;
    private final Map<String, Object> attributes; // 구글에서 받아온 정보를 관리


    // 구글에서 받아온 정보를 설정
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override // 허가된 권한 목록을 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        String role = member.getRole();
        log.info("userDetails ROLE:{}", role);

        // 컬렉션 객체에 사용자의 권한을 추가
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return role;            }
        });

        return collection;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았음을 의미
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았음을 의미
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호가 만료되지 않았음을 의미
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화되어 있음을 의미
    }

    @Override
    public String getName() {
        return "";
    }
}