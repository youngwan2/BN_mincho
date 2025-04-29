package com.mincho.herb.infra.auth;

import lombok.AllArgsConstructor;

import java.util.Map;


// 소셜 로그인 제공자 마다 데이터 추출 방식이 다르므로 별도 구성을 위해 추가
@AllArgsConstructor
public class GoogleUserDetailsCustom implements CustomOAuth2User {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }
}