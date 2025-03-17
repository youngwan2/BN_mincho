package com.mincho.herb.infra.auth;

import com.mincho.herb.domain.user.domain.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override // 허가된 권한 목록을 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();

        // 컬렉션 객체에 사용자의 권한을 추가
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole();
            }
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
}