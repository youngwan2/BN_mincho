package com.mincho.herb.infra.auth;

public interface CustomOAuth2User {

    String getProvider();
    String getProviderId();
    String getEmail();
    String getName();
}
