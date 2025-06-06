package com.mincho.herb.domain.banner.application;

public interface BannerEventService {
    void handleBannerClick(Long id);
    void handleBannerView(Long id);
}

